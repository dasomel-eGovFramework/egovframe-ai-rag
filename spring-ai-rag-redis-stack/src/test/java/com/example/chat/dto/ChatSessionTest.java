package com.example.chat.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChatSession 단위 테스트")
class ChatSessionTest {

    @Test
    @DisplayName("3인자 생성자는 lastMessageAt을 createdAt과 동일하게 설정")
    void threeArgConstructorSetsLastMessageAtToCreatedAt() {
        LocalDateTime ts = LocalDateTime.of(2025, 3, 10, 9, 0, 0);
        ChatSession session = new ChatSession("sess-001", "첫 번째 세션", ts);

        assertThat(session.getSessionId()).isEqualTo("sess-001");
        assertThat(session.getTitle()).isEqualTo("첫 번째 세션");
        assertThat(session.getCreatedAt()).isEqualTo(ts);
        assertThat(session.getLastMessageAt()).isEqualTo(ts);
    }

    @Test
    @DisplayName("전체 생성자로 lastMessageAt을 별도 지정")
    void allArgsConstructorAllowsDifferentLastMessageAt() {
        LocalDateTime created = LocalDateTime.of(2025, 1, 1, 8, 0, 0);
        LocalDateTime last = LocalDateTime.of(2025, 1, 2, 18, 30, 0);

        ChatSession session = new ChatSession("sess-002", "두 번째 세션", created, last);

        assertThat(session.getCreatedAt()).isEqualTo(created);
        assertThat(session.getLastMessageAt()).isEqualTo(last);
        assertThat(session.getLastMessageAt()).isAfter(session.getCreatedAt());
    }

    @Test
    @DisplayName("기본 생성자 후 setter로 필드 설정")
    void noArgConstructorWithSetters() {
        ChatSession session = new ChatSession();
        session.setSessionId("sess-003");
        session.setTitle("제목 없음");

        assertThat(session.getSessionId()).isEqualTo("sess-003");
        assertThat(session.getTitle()).isEqualTo("제목 없음");
    }

    @Test
    @DisplayName("Lombok @Data — 동일 값이면 equals는 true")
    void equalsForSameValues() {
        LocalDateTime ts = LocalDateTime.of(2025, 6, 1, 0, 0, 0);
        ChatSession s1 = new ChatSession("s1", "제목", ts, ts);
        ChatSession s2 = new ChatSession("s1", "제목", ts, ts);

        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
    }

    @Test
    @DisplayName("sessionId가 다르면 equals는 false")
    void differentSessionIdNotEqual() {
        LocalDateTime ts = LocalDateTime.now();
        ChatSession s1 = new ChatSession("id-A", "제목", ts, ts);
        ChatSession s2 = new ChatSession("id-B", "제목", ts, ts);

        assertThat(s1).isNotEqualTo(s2);
    }

    @Test
    @DisplayName("lastMessageAt setter로 업데이트 가능")
    void updateLastMessageAt() {
        LocalDateTime created = LocalDateTime.of(2025, 4, 1, 10, 0, 0);
        LocalDateTime updated = LocalDateTime.of(2025, 4, 1, 15, 0, 0);

        ChatSession session = new ChatSession("sess-004", "업데이트 세션", created);
        session.setLastMessageAt(updated);

        assertThat(session.getLastMessageAt()).isEqualTo(updated);
        assertThat(session.getCreatedAt()).isEqualTo(created);
    }
}
