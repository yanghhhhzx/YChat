package com.ychat.gateway.filter;

import com.ychat.common.exception.UnauthorizedException;
import com.ychat.common.utils.CollUtils;
import com.ychat.gateway.config.AuthProperties;
import com.ychat.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class LoginGatewayFilterFactory extends AbstractGatewayFilterFactory {

    private final JwtTool jwtTool;

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 检验是否已经登录，如果未登录，那么就只等访问“登录界面”
     * 可以访问的界面设置在了application
     */
    @Override
    public GatewayFilter apply(Object config) {
        return new OrderedGatewayFilter(new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                // 1.获取Request
                //ServerWebExchange是只能接受http请求的上下文的。

                ServerHttpRequest request = exchange.getRequest();
                // 2.判断是否不需要拦截
                if (isExclude(request.getPath().toString())) {
                    // 无需拦截，直接放行
                    return chain.filter(exchange);
                }
                // 3.获取请求头中的token
                String token = null;
                List<String> headers = request.getHeaders().get("authorization");
                if (!CollUtils.isEmpty(headers)) {
                    token = headers.get(0);
                }
                // 4.校验并解析token
                Long userId = null;
                try {
                    userId = jwtTool.parseToken(token);
                } catch (UnauthorizedException e) {
                    // 如果无效，拦截
                    ServerHttpResponse response = exchange.getResponse();
                    response.setRawStatusCode(401);
                    return response.setComplete();
                }

                // 5.如果有效，传递用户信息
                String userinfo = userId.toString();
                ServerWebExchange ex = exchange.mutate()
                        .request(b -> b.header("user-info", userinfo))
                        .build();
                //在请求头加入一个新的字段user-info，后面的微服务就不需要看token了，直接看userinfo
                // 6.放行
                return chain.filter(exchange);
            }
        },0);
    }


    /**
     * 判断是否需要拦截
     * @param antPath
     * @return
     */
    private boolean isExclude(String antPath) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if(antPathMatcher.match(pathPattern, antPath)){
                return true;
            }
        }
        return false;
    }

}


