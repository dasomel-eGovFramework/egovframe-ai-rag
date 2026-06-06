package com.example.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

/**
 * LLM 호출 예외를 분류하고 표준 폴백 메시지를 반환하는 핸들러
 *
 * <p>예외 유형을 타임아웃 / 토큰 초과 / 연결 오류 / 일반 오류로 분류하여
 * 서비스 계층에서 일관된 폴백 메시지를 제공한다.
 * 외부 라이브러리 의존성 없이 순수 Java + Spring으로 구현되어 있다.</p>
 */
@Slf4j
@Component
public class EgovAiFallbackHandler {

    /** 타임아웃 관련 예외 메시지 키워드 */
    private static final String[] TIMEOUT_KEYWORDS = {
            "timeout", "timed out", "read timed out", "connection timed out"
    };

    /** 토큰 초과 관련 예외 메시지 키워드 */
    private static final String[] TOKEN_KEYWORDS = {
            "token", "context length", "maximum context", "too many tokens",
            "prompt is too long", "input too long", "exceeds the limit"
    };

    /** 연결 오류 관련 예외 메시지 키워드 */
    private static final String[] CONNECTION_KEYWORDS = {
            "connection refused", "connection reset", "no route to host",
            "connect exception", "network is unreachable", "failed to connect"
    };

    /**
     * 예외 유형을 나타내는 열거형
     */
    public enum ErrorType {
        /** LLM 응답 대기 중 시간 초과 */
        TIMEOUT,
        /** 입력 토큰이 모델 허용 한도를 초과 */
        TOKEN_EXCEEDED,
        /** LLM 서버(Ollama 등)와의 연결 실패 */
        CONNECTION_ERROR,
        /** 위 세 유형에 해당하지 않는 일반 오류 */
        GENERAL_ERROR
    }

    /**
     * 예외 유형을 분류한다.
     *
     * @param throwable 발생한 예외
     * @return 분류된 {@link ErrorType}
     */
    public ErrorType classify(Throwable throwable) {
        if (throwable == null) {
            return ErrorType.GENERAL_ERROR;
        }

        // 예외 클래스 타입 기반 분류 (우선)
        if (throwable instanceof TimeoutException
                || throwable instanceof SocketTimeoutException) {
            log.debug("타임아웃 예외 분류: {}", throwable.getClass().getSimpleName());
            return ErrorType.TIMEOUT;
        }
        if (throwable instanceof ConnectException) {
            log.debug("연결 오류 예외 분류: {}", throwable.getClass().getSimpleName());
            return ErrorType.CONNECTION_ERROR;
        }

        // 예외 체인 전체를 순회하며 메시지 기반 분류
        Throwable current = throwable;
        while (current != null) {
            ErrorType type = classifyByMessage(current.getMessage());
            if (type != ErrorType.GENERAL_ERROR) {
                return type;
            }
            current = current.getCause();
        }

        return ErrorType.GENERAL_ERROR;
    }

    /**
     * 예외 유형에 맞는 표준 폴백 메시지를 반환한다.
     *
     * @param throwable 발생한 예외
     * @return 사용자에게 전달할 폴백 메시지
     */
    public String getFallbackMessage(Throwable throwable) {
        ErrorType type = classify(throwable);
        String message = buildFallbackMessage(type);
        log.warn("LLM 폴백 처리 - 오류 유형: {}, 원인: {}", type, summarize(throwable));
        return message;
    }

    /**
     * 예외 유형에 맞는 표준 폴백 메시지를 반환한다 (ErrorType 직접 지정).
     *
     * @param type 오류 유형
     * @return 사용자에게 전달할 폴백 메시지
     */
    public String getFallbackMessage(ErrorType type) {
        return buildFallbackMessage(type);
    }

    // ---- private helpers ----

    private ErrorType classifyByMessage(String message) {
        if (message == null) {
            return ErrorType.GENERAL_ERROR;
        }
        String lower = message.toLowerCase();

        for (String keyword : TIMEOUT_KEYWORDS) {
            if (lower.contains(keyword)) {
                return ErrorType.TIMEOUT;
            }
        }
        for (String keyword : TOKEN_KEYWORDS) {
            if (lower.contains(keyword)) {
                return ErrorType.TOKEN_EXCEEDED;
            }
        }
        for (String keyword : CONNECTION_KEYWORDS) {
            if (lower.contains(keyword)) {
                return ErrorType.CONNECTION_ERROR;
            }
        }
        return ErrorType.GENERAL_ERROR;
    }

    private String buildFallbackMessage(ErrorType type) {
        return switch (type) {
            case TIMEOUT -> "AI 모델 응답 시간이 초과되었습니다. 잠시 후 다시 시도해 주세요.";
            case TOKEN_EXCEEDED -> "입력 내용이 너무 길어 처리할 수 없습니다. 질문을 줄여서 다시 시도해 주세요.";
            case CONNECTION_ERROR -> "AI 모델 서버에 연결할 수 없습니다. 서버 상태를 확인한 후 다시 시도해 주세요.";
            case GENERAL_ERROR -> "AI 응답 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
        };
    }

    private String summarize(Throwable throwable) {
        if (throwable == null) {
            return "null";
        }
        String message = throwable.getMessage();
        return throwable.getClass().getSimpleName()
                + (message != null ? ": " + (message.length() > 100 ? message.substring(0, 100) + "..." : message) : "");
    }
}
