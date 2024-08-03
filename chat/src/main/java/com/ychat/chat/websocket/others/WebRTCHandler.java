package com.ychat.chat.websocket.others;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.HashMap;
import java.util.Map;

public class WebRTCHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private Map<String, String> sdpMap = new HashMap<>();

    private final TextWebSocketFrame welcomeMessage = new TextWebSocketFrame("Welcome to WebRTC Chat!");

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(welcomeMessage);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            String text = textFrame.text();

            if (text.startsWith("SDP:offer:")) {
                String peerId = text.split(":")[2];
                sdpMap.put(peerId, text.substring("SDP:offer:".length()));
                // 发送 SDP:answer 给对方
                String answer = "SDP:answer:" + generateAnswerSDP();
                ctx.writeAndFlush(new TextWebSocketFrame(answer));
            } else if (text.startsWith("ICE:")) {
                String iceCandidate = text.substring("ICE:".length());
                // 处理 ICE 候选信息
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private String generateAnswerSDP() {
        // 生成回答的 SDP 内容
        return "answerSDPContent";
    }
}