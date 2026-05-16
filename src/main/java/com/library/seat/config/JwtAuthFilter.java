package com.library.seat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.seat.common.JwtUtil;
import com.library.seat.common.Result;
import com.library.seat.entity.User;
import com.library.seat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (isPublicApi(uri) || !uri.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            sendUnauthorized(response, "未登录或 Token 已过期");
            return;
        }

        try {
            String rawToken = token.substring(7);
            if (!jwtUtil.validateToken(rawToken)) {
                sendUnauthorized(response, "Token 无效");
                return;
            }

            Long userId = jwtUtil.getUserId(rawToken);
            User user = userService.getById(userId);
            if (user == null) {
                sendUnauthorized(response, "用户不存在");
                return;
            }
            if (user.getStatus() != null && user.getStatus() == 0) {
                sendUnauthorized(response, "账号已被禁用");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendUnauthorized(response, "Token 解析失败");
        }
    }

    private boolean isPublicApi(String uri) {
        return uri.startsWith("/api/user/login")
            || uri.startsWith("/api/user/register")
            || uri.startsWith("/api/banner/list")
            || uri.startsWith("/api/announcement/list")
            || uri.startsWith("/api/announcement/")
            || uri.startsWith("/api/room/list")
            || uri.matches("^/api/room/\\d+$")
            || uri.startsWith("/uploads/");
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, message)));
    }
}
