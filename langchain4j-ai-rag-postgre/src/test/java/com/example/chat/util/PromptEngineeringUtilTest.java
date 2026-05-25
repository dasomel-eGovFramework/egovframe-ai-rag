package com.example.chat.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PromptEngineeringUtil 단위 테스트")
class PromptEngineeringUtilTest {

    @Test
    @DisplayName("Zero-shot 프롬프트는 한국어 응답 지시를 포함한다")
    void createZeroShotPrompt_containsKoreanResponseInstruction() {
        String prompt = PromptEngineeringUtil.createZeroShotPrompt();

        assertThat(prompt).isNotBlank();
        assertThat(prompt).contains("Respond in Korean");
    }

    @Test
    @DisplayName("컨텍스트 기반 프롬프트에 컨텍스트 내용이 포함된다")
    void createContextBasedPrompt_withContext_includesContextInPrompt() {
        String context = "eGovFrame은 공공기관 웹 서비스 개발을 위한 표준 프레임워크이다.";

        String prompt = PromptEngineeringUtil.createContextBasedPrompt(context);

        assertThat(prompt).contains(context);
        assertThat(prompt).contains("Respond in Korean");
    }

    @Test
    @DisplayName("Few-shot 프롬프트에 컨텍스트와 예시가 포함된다")
    void createFewShotLearningPrompt_withContext_includesContextAndExamples() {
        String context = "Spring Boot 관련 문서 내용";

        String prompt = PromptEngineeringUtil.createFewShotLearningPrompt(context);

        assertThat(prompt).contains(context);
        assertThat(prompt).contains("Example 1");
        assertThat(prompt).contains("Example 2");
    }

    @Test
    @DisplayName("코드 생성 프롬프트에 언어와 요구사항이 포함된다")
    void createCodeGenerationPrompt_withLanguageAndRequirement_includesBoth() {
        String language = "Java";
        String requirement = "파일을 읽어 MD5 해시를 계산하는 유틸리티";

        String prompt = PromptEngineeringUtil.createCodeGenerationPrompt(language, requirement);

        assertThat(prompt).contains(language);
        assertThat(prompt).contains(requirement);
    }

    @Test
    @DisplayName("역할 기반 프롬프트에 역할과 작업이 포함된다")
    void createRoleBasedPrompt_withRoleAndTask_includesBoth() {
        String role = "보안 전문가";
        String task = "의존성 취약점 분석";

        String prompt = PromptEngineeringUtil.createRoleBasedPrompt(role, task);

        assertThat(prompt).contains(role);
        assertThat(prompt).contains(task);
    }

    @Test
    @DisplayName("동적 Few-shot 프롬프트에 예시 질문과 답변이 포함된다")
    void createDynamicFewShotPrompt_withExamples_includesQuestionsAndAnswers() {
        String context = "기술 문서 컨텍스트";
        List<Map.Entry<String, String>> examples = List.of(
            Map.entry("Spring Boot란?", "Spring Boot는 자동 구성을 지원하는 프레임워크입니다.")
        );

        String prompt = PromptEngineeringUtil.createDynamicFewShotPrompt(context, examples);

        assertThat(prompt).contains(context);
        assertThat(prompt).contains("Spring Boot란?");
        assertThat(prompt).contains("Spring Boot는 자동 구성을 지원하는 프레임워크입니다.");
    }
}
