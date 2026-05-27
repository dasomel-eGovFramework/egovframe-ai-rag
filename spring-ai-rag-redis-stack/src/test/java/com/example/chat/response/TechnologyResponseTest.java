package com.example.chat.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TechnologyResponse 단위 테스트")
class TechnologyResponseTest {

    @Test
    @DisplayName("기본 생성자 후 setter로 필드 설정")
    void noArgConstructorAndSetters() {
        TechnologyResponse response = new TechnologyResponse();
        response.setName("Spring Boot");
        response.setCategory("Framework");
        response.setDescription("Java 기반 웹 프레임워크");
        response.setFeatures(List.of("자동 구성", "내장 서버"));
        response.setUseCases(List.of("REST API", "마이크로서비스"));

        assertThat(response.getName()).isEqualTo("Spring Boot");
        assertThat(response.getCategory()).isEqualTo("Framework");
        assertThat(response.getDescription()).isEqualTo("Java 기반 웹 프레임워크");
        assertThat(response.getFeatures()).containsExactly("자동 구성", "내장 서버");
        assertThat(response.getUseCases()).containsExactly("REST API", "마이크로서비스");
    }

    @Test
    @DisplayName("전체 생성자로 모든 필드 초기화")
    void allArgsConstructorInitializesAllFields() {
        List<String> features = List.of("컨테이너 오케스트레이션", "자동 스케일링");
        List<String> useCases = List.of("클라우드 배포", "무중단 운영");

        TechnologyResponse response = new TechnologyResponse(
                "Kubernetes", "Infra", "컨테이너 오케스트레이션 플랫폼", features, useCases);

        assertThat(response.getName()).isEqualTo("Kubernetes");
        assertThat(response.getCategory()).isEqualTo("Infra");
        assertThat(response.getFeatures()).hasSize(2);
        assertThat(response.getUseCases()).hasSize(2);
    }

    @Test
    @DisplayName("Lombok @Data — equals/hashCode는 모든 필드 기준")
    void equalsBasedOnAllFields() {
        List<String> features = List.of("기능1");
        List<String> useCases = List.of("활용1");

        TechnologyResponse r1 = new TechnologyResponse("Redis", "DB", "인메모리 저장소", features, useCases);
        TechnologyResponse r2 = new TechnologyResponse("Redis", "DB", "인메모리 저장소", features, useCases);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    @DisplayName("name이 다르면 equals는 false")
    void differentNameProducesNotEqual() {
        TechnologyResponse r1 = new TechnologyResponse("Redis", "DB", "설명", null, null);
        TechnologyResponse r2 = new TechnologyResponse("Kafka", "DB", "설명", null, null);

        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    @DisplayName("features와 useCases가 null이어도 객체 생성 가능")
    void nullListsAllowed() {
        TechnologyResponse response = new TechnologyResponse("Test", "Cat", "Desc", null, null);

        assertThat(response.getFeatures()).isNull();
        assertThat(response.getUseCases()).isNull();
    }

    @Test
    @DisplayName("Lombok @Data — toString은 클래스명과 필드를 포함")
    void toStringContainsFieldValues() {
        TechnologyResponse response = new TechnologyResponse(
                "Docker", "Infra", "컨테이너 플랫폼", List.of("경량화"), List.of("CI/CD"));
        String str = response.toString();

        assertThat(str).contains("Docker").contains("Infra");
    }
}
