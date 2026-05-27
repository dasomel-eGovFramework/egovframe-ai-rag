package com.example.chat.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DocumentStatusResponse 단위 테스트")
class DocumentStatusResponseTest {

    @Test
    @DisplayName("전체 생성자로 모든 필드 설정")
    void fullConstructorSetsAllFields() {
        DocumentStatusResponse response = new DocumentStatusResponse(true, 5, 10, 3, true);

        assertThat(response.processing()).isTrue();
        assertThat(response.processedCount()).isEqualTo(5);
        assertThat(response.totalCount()).isEqualTo(10);
        assertThat(response.changedCount()).isEqualTo(3);
        assertThat(response.hasDocuments()).isTrue();
    }

    @Test
    @DisplayName("3인자 생성자는 changedCount=0, hasDocuments=totalCount>0으로 초기화")
    void threeArgConstructorDefaults() {
        DocumentStatusResponse response = new DocumentStatusResponse(false, 3, 8);

        assertThat(response.processing()).isFalse();
        assertThat(response.processedCount()).isEqualTo(3);
        assertThat(response.totalCount()).isEqualTo(8);
        assertThat(response.changedCount()).isEqualTo(0);
        assertThat(response.hasDocuments()).isTrue();
    }

    @Test
    @DisplayName("3인자 생성자에서 totalCount=0이면 hasDocuments=false")
    void threeArgConstructorNoDocuments() {
        DocumentStatusResponse response = new DocumentStatusResponse(false, 0, 0);

        assertThat(response.hasDocuments()).isFalse();
    }

    @Test
    @DisplayName("4인자 생성자는 hasDocuments=totalCount>0으로 초기화")
    void fourArgConstructorSetsHasDocuments() {
        DocumentStatusResponse withDocs = new DocumentStatusResponse(true, 2, 5, 1);
        assertThat(withDocs.hasDocuments()).isTrue();

        DocumentStatusResponse withoutDocs = new DocumentStatusResponse(false, 0, 0, 0);
        assertThat(withoutDocs.hasDocuments()).isFalse();
    }

    @Test
    @DisplayName("처리 중 상태와 완료 상태 구분")
    void processingFlagDistinction() {
        DocumentStatusResponse inProgress = new DocumentStatusResponse(true, 2, 10, 2, true);
        DocumentStatusResponse done = new DocumentStatusResponse(false, 10, 10, 0, true);

        assertThat(inProgress.processing()).isTrue();
        assertThat(done.processing()).isFalse();
    }

    @Test
    @DisplayName("record equals — 동일한 값이면 같은 객체")
    void recordEquality() {
        DocumentStatusResponse r1 = new DocumentStatusResponse(false, 3, 3, 0, true);
        DocumentStatusResponse r2 = new DocumentStatusResponse(false, 3, 3, 0, true);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    @DisplayName("record toString은 모든 필드를 포함")
    void recordToStringContainsFields() {
        DocumentStatusResponse response = new DocumentStatusResponse(true, 1, 5, 2, true);
        String str = response.toString();

        assertThat(str).contains("processing=true")
                .contains("processedCount=1")
                .contains("totalCount=5")
                .contains("changedCount=2")
                .contains("hasDocuments=true");
    }
}
