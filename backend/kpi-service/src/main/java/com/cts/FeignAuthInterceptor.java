package com.cts;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attrs =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String token = attrs.getRequest().getHeader("Authorization");
            if (token != null) {
                template.header("Authorization", token);
            }

            String role = attrs.getRequest().getHeader("X-Auth-Role");
            if (role != null) {
                template.header("X-Auth-Role", role);
            }

            String user = attrs.getRequest().getHeader("X-Auth-User");
            if (user != null) {
                template.header("X-Auth-User", user);
            }

            String userId = attrs.getRequest().getHeader("X-Auth-UserId");
            if (userId != null) {
                template.header("X-Auth-UserId", userId);
            }
        }
    }
}