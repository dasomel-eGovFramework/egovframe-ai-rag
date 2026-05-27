package com.example.chat.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import java.time.temporal.ChronoUnit;

@DisplayName("ChatMessageDto 단위 테스트")
class ChatMessageDtoTest {

    @Test
    @DisplayName("2인자 생성자는 timestamp를 현재 시각으로 자동 설정")
    void twoArgConstructorSetsTimestampToNow() {
        LocalDateTime before = LocalDateTime.now();
        ChatMessageDto dto = new ChatMessageDto("USER", "안녕하세요");
        LocalDateTime after = LocalDateTime.now();

        assertThat(dto.getMessageType()).isEqualTo("USER");
        assertThat(dto.getContent()).isEqualTo("안녕하세요");
        assertThat(dto.getTimestamp())
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(after);
    }

    @Test
    @DisplayName("전체 생성자로 timestamp 직접 지정")
    void allArgsConstructorSetsExplicitTimestamp() {
        LocalDateTime ts = LocalDateTime.of(2025, 1, 15, 10, 30, 0);
        ChatMessageDto dto = new ChatMessageDto("ASSISTANT", "응답 내용", ts);

        assertThat(dto.getMessageType()).isEqualTo("ASSISTANT");
        assertThat(dto.getContent()).isEqualTo("응답 내용");
        assertThat(dto.getTimestamp()).isEqualTo(ts);
    }

    @Test
    @DisplayName("기본 생성자 후 setter로 필드 설정")
    void noArgConstructorWithSetters() {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setMessageType("SYSTEM");
        dto.setContent("시스템 메시지");

        assertThat(dto.getMessageType()).isEqualTo("SYSTEM");
        assertThat(dto.getContent()).isEqualTo("시스템 메시지");
    }

    @Test
    @DisplayName("Lombok @Data — 동일 값이면 equals는 true")
    void equalsForSameValues() {
        LocalDateTime ts = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
        ChatMessageDto d1 = new ChatMessageDto("USER", "내용", ts);
        ChatMessageDto d2 = new ChatMessageDto("USER", "내용", ts);

        assertThat(d1).isEqualTo(d2);
        assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
    }

    @Test
    @DisplayName("messageType이 다르면 equals는 false")
    void differentMessageTypeNotEqual() {
        LocalDateTime ts = LocalDateTime.now();
        ChatMessageDto d1 = new ChatMessageDto("USER", "내용", ts);
        ChatMessageDto d2 = new ChatMessageDto("ASSISTANT", "내용", ts);

        assertThat(d1).isNotEqualTo(d2);
    }

    @Test
    @DisplayName("content가 다르면 equals는 false")
    void differentContentNotEqual() {
        LocalDateTime ts = LocalDateTime.now();
        ChatMessageDto d1 = new ChatMessageDto("USER", "질문1", ts);
        ChatMessageDto d2 = new ChatMessageDto("USER", "질문2", ts);

        assertThat(d1).isNotEqualTo(d2);
    }
}
