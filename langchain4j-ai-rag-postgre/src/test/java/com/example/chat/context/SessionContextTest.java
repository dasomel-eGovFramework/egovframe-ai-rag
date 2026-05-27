package com.example.chat.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SessionContext 단위 테스트")
class SessionContextTest {

    @AfterEach
    void tearDown() {
        SessionContext.clear();
    }

    @Test
    @DisplayName("세션 ID 설정 후 조회 시 동일한 값 반환")
    void setAndGetSessionId() {
        SessionContext.setCurrentSessionId("session-001");
        assertThat(SessionContext.getCurrentSessionId()).isEqualTo("session-001");
    }

    @Test
    @DisplayName("세션 ID 미설정 시 기본값 반환")
    void getSessionIdReturnsDefaultWhenNotSet() {
        assertThat(SessionContext.getCurrentSessionId())
                .isEqualTo(SessionContext.DEFAULT_CONVERSATION_ID);
    }

    @Test
    @DisplayName("null 세션 ID 설정 시 기본값으로 대체")
    void setNullSessionIdFallsBackToDefault() {
        SessionContext.setCurrentSessionId(null);
        assertThat(SessionContext.getCurrentSessionId())
                .isEqualTo(SessionContext.DEFAULT_CONVERSATION_ID);
    }

    @Test
    @DisplayName("빈 문자열 세션 ID 설정 시 기본값으로 대체")
    void setEmptySessionIdFallsBackToDefault() {
        SessionContext.setCurrentSessionId("   ");
        assertThat(SessionContext.getCurrentSessionId())
                .isEqualTo(SessionContext.DEFAULT_CONVERSATION_ID);
    }

    @Test
    @DisplayName("clear() 호출 후 기본값 반환")
    void clearResetsToDefault() {
        SessionContext.setCurrentSessionId("session-xyz");
        SessionContext.clear();
        assertThat(SessionContext.getCurrentSessionId())
                .isEqualTo(SessionContext.DEFAULT_CONVERSATION_ID);
    }

    @Test
    @DisplayName("기본 세션일 때 isDefaultSession()은 true 반환")
    void isDefaultSessionReturnsTrueForDefaultId() {
        SessionContext.setCurrentSessionId(SessionContext.DEFAULT_CONVERSATION_ID);
        assertThat(SessionContext.isDefaultSession()).isTrue();
    }

    @Test
    @DisplayName("커스텀 세션 ID일 때 isDefaultSession()은 false 반환")
    void isDefaultSessionReturnsFalseForCustomId() {
        SessionContext.setCurrentSessionId("custom-session");
        assertThat(SessionContext.isDefaultSession()).isFalse();
    }

    @Test
    @DisplayName("스레드별 독립적인 세션 ID 유지")
    void threadLocalIsolation() throws InterruptedException {
        SessionContext.setCurrentSessionId("main-thread-session");

        String[] threadSessionId = new String[1];
        Thread thread = new Thread(() -> {
            // 별도 스레드에서는 main 스레드 값이 보이지 않아야 함
            threadSessionId[0] = SessionContext.getCurrentSessionId();
            SessionContext.clear();
        });
        thread.start();
        thread.join();

        assertThat(threadSessionId[0]).isEqualTo(SessionContext.DEFAULT_CONVERSATION_ID);
        assertThat(SessionContext.getCurrentSessionId()).isEqualTo("main-thread-session");
    }
}
