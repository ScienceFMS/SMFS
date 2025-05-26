package hit.cs.sciencefms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import hit.cs.sciencefms.common.Result;
import hit.cs.sciencefms.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * JWT过滤器
 */
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    
    /**
     * 不需要验证token的路径
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error",
            "/*.html",
            "/*.js",
            "/*.css",
            "/*.ico",
            "/static/**",
            "/assets/**"
    );
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        // 问题竟然出现在预检OPTION上，因为OPTIONS请求不会携带token，然后不通过浏览器就认为是跨域问题，直接不发送
        // 我的请求，还报错是跨域问题，这不是坑人吗。


        // 对于OPTIONS请求，直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String path = request.getRequestURI();
        
        // 检查是否为排除路径
        if (isExcludedPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 获取token
        String token = getTokenFromRequest(request);
        
        // 验证token
        if (token == null || !jwtUtil.validateToken(token)) {
            handleUnauthorized(response);
            return;
        }
        
        // 获取用户信息，可以在这里做更多的权限检查
        // String username = jwtUtil.getUsernameFromToken(token);
        // String role = jwtUtil.getRoleFromToken(token);
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
    
    /**
     * 检查是否为排除路径
     */
    private boolean isExcludedPath(String path) {
        for (String pattern : EXCLUDE_PATHS) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {

            System.out.println("bearerToken: " + bearerToken.substring(7));
            
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * 处理未授权的请求
     */
    private void handleUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(Result.unauthorized()));
        writer.flush();
    }
} 