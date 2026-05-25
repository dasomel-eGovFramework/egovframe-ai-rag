package com.example.chat.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EgovDocumentHashUtil 단위 테스트")
class EgovDocumentHashUtilTest {

    @Test
    @DisplayName("일반 문자열의 MD5 해시를 반환한다")
    void calculateHash_withNormalContent_returnsMd5Hash() {
        String content = "eGovFrame RAG 문서 내용";

        String hash = EgovDocumentHashUtil.calculateHash(content);

        assertThat(hash).isNotNull();
        assertThat(hash).hasSize(32);
        assertThat(hash).matches("[0-9a-f]{32}");
    }

    @Test
    @DisplayName("동일한 내용은 동일한 해시를 반환한다")
    void calculateHash_withSameContent_returnsSameHash() {
        String content = "동일한 문서 내용";

        String hash1 = EgovDocumentHashUtil.calculateHash(content);
        String hash2 = EgovDocumentHashUtil.calculateHash(content);

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("서로 다른 내용은 서로 다른 해시를 반환한다")
    void calculateHash_withDifferentContent_returnsDifferentHashes() {
        String content1 = "첫 번째 문서";
        String content2 = "두 번째 문서";

        String hash1 = EgovDocumentHashUtil.calculateHash(content1);
        String hash2 = EgovDocumentHashUtil.calculateHash(content2);

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("null 입력 시 빈 문자열을 반환한다")
    void calculateHash_withNullContent_returnsEmptyString() {
        String hash = EgovDocumentHashUtil.calculateHash(null);

        assertThat(hash).isEmpty();
    }

    @Test
    @DisplayName("빈 문자열 입력 시 빈 문자열을 반환한다")
    void calculateHash_withEmptyContent_returnsEmptyString() {
        String hash = EgovDocumentHashUtil.calculateHash("");

        assertThat(hash).isEmpty();
    }
}
