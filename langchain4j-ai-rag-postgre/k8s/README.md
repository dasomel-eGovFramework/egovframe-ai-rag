# Kubernetes 배포 운영 절차

`langchain4j-ai-rag-postgre` 모듈을 Kubernetes 클러스터에 배포하는 전체 절차를 안내한다.

---

## 사전 요건

| 항목 | 비고 |
|------|------|
| Docker 28+ | 이미지 빌드용 |
| kubectl | 클러스터 접근 |
| 실행 중인 Kubernetes 클러스터 | 로컬: minikube / kind, 운영: EKS·GKE·AKS 등 |
| 컨테이너 레지스트리 | 이미지 push 가능한 레지스트리 |
| PostgreSQL + PGVector | `postgres-pgvector` 서비스로 접근 가능해야 함 |
| Ollama | 클러스터 내 또는 외부에서 `11434` 포트 접근 가능해야 함 |

---

## 1. 이미지 빌드

```bash
# 프로젝트 루트(pom.xml이 있는 디렉터리)에서 실행
cd langchain4j-ai-rag-postgre

docker build \
  -t <레지스트리>/langchain4j-ai-rag-postgre:1.0.0 \
  .
```

빌드가 완료되면 이미지를 레지스트리에 push한다.

```bash
docker push <레지스트리>/langchain4j-ai-rag-postgre:1.0.0
```

---

## 2. 매니페스트 수정

### deployment.yaml — 이미지 경로 교체

`k8s/deployment.yaml`의 `image` 필드를 push한 이미지 경로로 변경한다.

```yaml
image: <레지스트리>/langchain4j-ai-rag-postgre:1.0.0
```

### configmap.yaml — 환경별 값 확인

`k8s/configmap.yaml`에서 아래 항목이 실제 클러스터 환경과 일치하는지 확인한다.

| 키 | 기본값 | 설명 |
|----|--------|------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres-pgvector:5432/ragdb` | PostgreSQL 서비스명·포트·DB명 |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | `org.postgresql.Driver` | 드라이버 클래스 |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | DDL 전략 (운영: `validate` 권장) |
| `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | `health,info` | Actuator 노출 엔드포인트 |

### Secret — DB 인증 정보 생성

DB 사용자명과 비밀번호는 Secret으로 관리한다. Secret 이름은 `langchain4j-ai-rag-postgre-db`이어야 한다.

```bash
kubectl create secret generic langchain4j-ai-rag-postgre-db \
  --from-literal=username=<DB_USERNAME> \
  --from-literal=password=<DB_PASSWORD>
```

### Ollama 연결 주소 설정 (환경변수 추가)

Ollama가 클러스터 외부에 있는 경우 `deployment.yaml`의 `env` 섹션에 아래 항목을 추가한다.

```yaml
env:
  - name: LANGCHAIN4J_OLLAMA_BASE_URL
    value: "http://<ollama-host>:11434"
```

클러스터 내 Ollama 서비스가 있으면 서비스 DNS 이름(예: `http://ollama:11434`)을 사용한다.

### ONNX 임베딩 모델 경로 설정

`app.embedding-config-path`는 파드 내 경로를 가리킨다. PersistentVolumeClaim 또는 ConfigMap/Secret을 통해 아래 환경변수로 재정의한다.

```yaml
env:
  - name: APP_EMBEDDING_CONFIG_PATH
    value: "/config/embeddingConfig.json"
```

---

## 3. 배포

```bash
# ConfigMap 먼저 적용
kubectl apply -f k8s/configmap.yaml

# Deployment · Service 적용
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

---

## 4. 상태 확인

```bash
# 파드 상태 확인
kubectl get pods -l app.kubernetes.io/name=langchain4j-ai-rag-postgre

# 파드 로그 확인
kubectl logs -l app.kubernetes.io/name=langchain4j-ai-rag-postgre --tail=100

# Deployment 롤아웃 상태
kubectl rollout status deployment/langchain4j-ai-rag-postgre

# Actuator health 확인 (파드 내부에서)
kubectl exec -it <pod-name> -- wget -qO- http://127.0.0.1:8080/actuator/health
```

---

## 5. 접속

Service 타입이 `ClusterIP`이므로 클러스터 외부에서 직접 접근할 수 없다. 아래 방법 중 하나를 선택한다.

### 방법 A: kubectl port-forward (개발·디버그용)

```bash
kubectl port-forward svc/langchain4j-ai-rag-postgre 8080:8080
# 이후 http://localhost:8080 으로 접속
```

### 방법 B: NodePort로 Service 타입 변경 (테스트 환경)

`k8s/service.yaml`에서 `type: ClusterIP`를 `type: NodePort`로 변경하고 재적용한다.

```yaml
spec:
  type: NodePort
  ports:
    - name: http
      port: 8080
      targetPort: http
      nodePort: 30080   # 30000–32767 범위
```

```bash
kubectl apply -f k8s/service.yaml
# 접속: http://<NodeIP>:30080
```

### 방법 C: Ingress 사용 (운영 환경 권장)

Ingress Controller(nginx 등)가 설치된 경우 Ingress 리소스를 별도로 생성한다.

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: langchain4j-ai-rag-postgre
spec:
  rules:
    - host: rag.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: langchain4j-ai-rag-postgre
                port:
                  number: 8080
```

---

## 6. 주요 포트 및 환경변수 요약

| 항목 | 값 | 설명 |
|------|----|------|
| 애플리케이션 포트 | `8080` | HTTP, EXPOSE 및 containerPort |
| Readiness probe | `GET /actuator/health/readiness` | 트래픽 수신 준비 여부 |
| Liveness probe | `GET /actuator/health/liveness` | 재시작 필요 여부 |
| PostgreSQL 서비스명 | `postgres-pgvector` | 클러스터 내 DNS |
| PostgreSQL 포트 | `5432` | |
| DB 이름 | `ragdb` | |
| Ollama 기본 포트 | `11434` | |
| ONNX 임베딩 설정 파일 | `${user.home}/langchain4j-Config/Config/embeddingConfig.json` | 파드 내 마운트 경로로 재정의 가능 |

---

## 7. 업데이트 배포 (롤링 업데이트)

새 이미지를 빌드·push한 후 이미지 태그를 갱신하여 재적용한다.

```bash
docker build -t <레지스트리>/langchain4j-ai-rag-postgre:1.0.1 .
docker push <레지스트리>/langchain4j-ai-rag-postgre:1.0.1

# deployment.yaml의 image 태그 수정 후
kubectl apply -f k8s/deployment.yaml

# 롤아웃 진행 상황 확인
kubectl rollout status deployment/langchain4j-ai-rag-postgre
```

롤백이 필요한 경우:

```bash
kubectl rollout undo deployment/langchain4j-ai-rag-postgre
```

---

## 8. 리소스 정리

```bash
kubectl delete -f k8s/service.yaml
kubectl delete -f k8s/deployment.yaml
kubectl delete -f k8s/configmap.yaml
kubectl delete secret langchain4j-ai-rag-postgre-db
```
