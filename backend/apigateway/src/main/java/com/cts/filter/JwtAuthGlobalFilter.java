package com.cts.filter;
 
import java.nio.charset.StandardCharsets;
 
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
 
import com.cts.util.GatewayJwtUtil;
 
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;
 
@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {
 
    private final GatewayJwtUtil jwtUtil;
 
    public JwtAuthGlobalFilter(GatewayJwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
 
        String path = exchange.getRequest().getPath().toString();
        String authHeader = exchange.getRequest()
                            .getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
 
        if (path.startsWith("/api/auth/")
                || path.startsWith("/internal/")
                || path.startsWith("/fallback/")) {
            System.out.println("[GATEWAY] Public path — skipping JWT: " + path);
            return chain.filter(exchange);
        }
 
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[GATEWAY] No token for path: " + path);
            return sendUnauthorized(exchange);
        }
 
        try {
            String token = authHeader.substring(7);
            Claims claims = jwtUtil.validateAndExtract(token);
 
            if (claims == null) {
                System.out.println("[GATEWAY] Token invalid — null claims for path: " + path);
                return sendUnauthorized(exchange);
            }
 
            // Extract user info from token claims
            String username  = claims.getSubject();
            String role      = claims.get("role", String.class);
            Object userIdObj = claims.get("userId");
            String userId    = userIdObj != null ? userIdObj.toString() : "";
 
            System.out.println("[GATEWAY] Path: " + path);
            System.out.println("[GATEWAY] X-Auth-User: " + username);
            System.out.println("[GATEWAY] X-Auth-Role: " + role);
            System.out.println("[GATEWAY] X-Auth-UserId: " + userId);
 
            // Inject user info into downstream request headers
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-Auth-User", username)
                    .header("X-Auth-Role", role)
                    .header("X-Auth-UserId", userId)
                    .build();
 
            return chain.filter(exchange.mutate().request(mutated).build());
 
        } catch (Exception e) {
            System.out.println("[GATEWAY] Token validation failed: " + e.getMessage());
            return sendUnauthorized(exchange);
        }
    }
 
    private Mono<Void> sendUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        String body = "{\"error\":\"UNAUTHORIZED\",\"message\":\"Invalid or missing token\"}";
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
 
    @Override
    public int getOrder() {
        return -1;
    }
}