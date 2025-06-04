package hit.cs.sciencefms.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
public class LangChain4jConfig {

    @Value("${langchain4j.qianfan.chat-model.api-key}")
    private String qianfanApiKey;
    
    @Value("${langchain4j.qianfan.chat-model.secret-key}")
    private String qianfanSecretKey;
    
    @Value("${langchain4j.qianfan.chat-model.model-name}")
    private String qianfanModelName;
    
    @Value("${langchain4j.qianfan.chat-model.temperature}")
    private Double qianfanTemperature;
    
    @Value("${langchain4j.qianfan.chat-model.timeout}")
    private Duration qianfanTimeout;

    /**
     * 配置百度千帆模型作为默认聊天模型
     */
    @Bean
    @Primary
    public ChatLanguageModel qianfanChatLanguageModel() {
        return QianfanChatModel.builder()
                .apiKey(qianfanApiKey)
                .secretKey(qianfanSecretKey)
                .modelName(qianfanModelName)
                .temperature(qianfanTemperature)
                .maxRetries(3)
                .build();
    }
    
} 