package hit.cs.sciencefms.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CORS过滤器
 * 在所有过滤器之前处理跨域请求
 * 在Spring Boot中，任何带有@Component注解的Filter实现类会被自动注册到Servlet容器
 * @Order注解决定了过滤器的执行顺序
 */
@Component
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 设置允许跨域的域名
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        // 设置允许的请求方法
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // 设置允许的请求头
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        // 设置允许携带认证信息
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 设置预检请求的缓存时间
        response.setHeader("Access-Control-Max-Age", "3600");

        // 对于OPTIONS请求，直接返回
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // 继续其他过滤器
            chain.doFilter(req, res);
        }
    }
} 