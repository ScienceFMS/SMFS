package hit.cs.sciencefms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtFilter jwtFilter;
    
    /**
     * 注册JWT过滤器，应用到所有需要登录验证的URLs
     */
    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtFilter);
        registration.addUrlPatterns("/*"); // 应用到所有路径
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10); // 设置优先级
        return registration;
    }
    
    /**
     * 配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")  // 指定前端域名，不能与allowCredentials=true同时使用通配符
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // 允许发送cookie
                .maxAge(3600);
    }
} 