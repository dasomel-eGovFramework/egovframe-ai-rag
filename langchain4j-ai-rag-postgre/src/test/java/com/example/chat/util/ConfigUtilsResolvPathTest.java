package com.example.chat.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConfigUtils.resolvePath() 단위 테스트")
class ConfigUtilsResolvPathTest {

    private ConfigUtils configUtils;

    @BeforeEach
    void setUp() {
        configUtils = new ConfigUtils();
    }

    @Test
    @DisplayName("null 입력 시 null 반환")
    void resolvePathWithNullReturnsNull() {
        assertThat(configUtils.resolvePath(null)).isNull();
    }

    @Test
    @DisplayName("빈 문자열 입력 시 빈 문자열 반환")
    void resolvePathWithEmptyStringReturnsEmpty() {
        assertThat(configUtils.resolvePath("")).isEmpty();
    }

    @Test
    @DisplayName("${HOME} 없는 경로는 그대로 정규화하여 반환")
    void resolvePathWithoutHomePlaceholderNormalizesPath() {
        String result = configUtils.resolvePath("/some/path/to/file");
        assertThat(result).isNotNull();
        assertThat(result).contains("some");
        assertThat(result).contains("file");
    }

    @Test
    @DisplayName("${HOME} 플레이스홀더를 실제 홈 디렉토리로 교체")
    void resolvePathReplacesHomeVariable() {
        String expectedHome = System.getenv("HOME");
        if (expectedHome == null || expectedHome.isEmpty()) {
            expectedHome = System.getenv("USERPROFILE");
        }
        if (expectedHome == null || expectedHome.isEmpty()) {
            expectedHome = System.getProperty("user.home");
        }

        String result = configUtils.resolvePath("${HOME}/models/model.onnx");

        assertThat(result).isNotNull();
        assertThat(result).doesNotContain("${HOME}");
        assertThat(result).contains("models");
        assertThat(result).contains("model.onnx");
        if (expectedHome != null) {
            assertThat(result).startsWith(expectedHome.replace('\\', File.separatorChar));
        }
    }

    @Test
    @DisplayName("백슬래시를 시스템 구분자로 정규화")
    @DisabledOnOs(OS.WINDOWS)
    void resolvePathNormalizesBackslashes() {
        String result = configUtils.resolvePath("/some\\path\\to\\file");
        assertThat(result).doesNotContain("\\");
        assertThat(result).contains("/");
    }

    @Test
    @DisplayName("중복 구분자 경로 정규화")
    void resolvePathNormalizesRedundantSegments() {
        String result = configUtils.resolvePath("/some/path/../path/to/file");
        assertThat(result).isNotNull();
        assertThat(result).doesNotContain("..");
    }
}
