package com.example.chat.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EgovResponseCleanerUtil 단위 테스트")
class EgovResponseCleanerUtilTest {

    @Test
    @DisplayName("think 태그와 내용을 제거하고 JSON만 반환한다")
    void cleanResponse_withThinkTag_removesThinkTagAndExtractsJson() {
        String response = "<think>내부 추론 과정</think>{\"name\": \"Spring Boot\"}";

        String cleaned = EgovResponseCleanerUtil.cleanResponse(response);

        assertThat(cleaned).isEqualTo("{\"name\": \"Spring Boot\"}");
        assertThat(cleaned).doesNotContain("<think>");
    }

    @Test
    @DisplayName("think 태그 없이 JSON만 있으면 그대로 반환한다")
    void cleanResponse_withoutThinkTag_returnsJsonAsIs() {
        String response = "{\"name\": \"eGovFrame\", \"category\": \"프레임워크\"}";

        String cleaned = EgovResponseCleanerUtil.cleanResponse(response);

        assertThat(cleaned).isEqualTo("{\"name\": \"eGovFrame\", \"category\": \"프레임워크\"}");
    }

    @Test
    @DisplayName("여러 줄에 걸친 think 태그도 제거한다")
    void cleanResponse_withMultilineThinkTag_removesThinkTag() {
        String response = "<think>\n1단계 분석\n2단계 검토\n</think>{\"answer\": \"결과\"}";

        String cleaned = EgovResponseCleanerUtil.cleanResponse(response);

        assertThat(cleaned).isEqualTo("{\"answer\": \"결과\"}");
    }

    @Test
    @DisplayName("null 입력 시 null을 반환한다")
    void cleanResponse_withNullInput_returnsNull() {
        String cleaned = EgovResponseCleanerUtil.cleanResponse(null);

        assertThat(cleaned).isNull();
    }

    @Test
    @DisplayName("빈 문자열 입력 시 빈 문자열을 반환한다")
    void cleanResponse_withEmptyInput_returnsEmptyString() {
        String cleaned = EgovResponseCleanerUtil.cleanResponse("");

        assertThat(cleaned).isEmpty();
    }

    @Test
    @DisplayName("JSON 앞뒤 공백을 제거한다")
    void cleanResponse_withSurroundingWhitespace_trimmed() {
        String response = "  {\"key\": \"value\"}  ";

        String cleaned = EgovResponseCleanerUtil.cleanResponse(response);

        assertThat(cleaned).isEqualTo("{\"key\": \"value\"}");
    }
}
