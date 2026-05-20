package com.example.chat;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class Langchain4jRagApplicationTests {

    @MockBean
    private EmbeddingModel embeddingModel;

    @MockBean
    private OllamaChatModel chatLanguageModel;

    @MockBean
    private OllamaStreamingChatModel streamingChatLanguageModel;

    @MockBean
    private EmbeddingStore<TextSegment> embeddingStore;

    @Test
    void contextLoads() {
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public static BeanFactoryPostProcessor removeRealConfig() {
            return beanFactory -> {
                if (beanFactory instanceof BeanDefinitionRegistry) {
                    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                    if (registry.containsBeanDefinition("egovLangChain4jConfig")) {
                        registry.removeBeanDefinition("egovLangChain4jConfig");
                    }
                }
            };
        }
    }
}
