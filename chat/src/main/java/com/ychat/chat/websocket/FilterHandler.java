package com.ychat.chat.websocket;

import com.ychat.chat.utils.JwtTool;
import com.ychat.common.exception.UnauthorizedException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.springframework.stereotype.Component;


@Component
public class FilterHandler extends ChannelInboundHandlerAdapter {

    private final JwtTool jwtTool;

    public FilterHandler(JwtTool jwtTool) {
        this.jwtTool = jwtTool;
    }
    // 已测试通过
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            // 3.获取请求头中的token
            String token = null;
            String headers = request.headers().get("authorization");
            if (!(headers ==null)) {
                token = headers;
            }
            // 4.校验并解析token
            Long userId = null;
            try {
                userId = jwtTool.parseToken(token);
            } catch (UnauthorizedException e) {
                // 如果无效，拦截
                System.out.println("token无效");
//                channelGroup.remove(ctx);
                //改用concurrentHashmap了，上面那个不需要了,因为现在加入channels是在后面，在这里就不需要移除了
                super.channelInactive(ctx);
            }
            // 5.如果有效，传递用户信息
            String userinfo = userId.toString();
            request.headers().set("user-info",userinfo);
            super.channelRead(ctx, request);
        }
        else {
            super.channelRead(ctx, msg);
        }
    }
}
