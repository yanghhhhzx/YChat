package com.ychat.chat.utils;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;


@Component
public class ChannelContext {

    //在channel中添加一个名为keyName的key并赋值为userId
    //在外部我保存的用户id的keyName为"userId"
    public static void addcontext(Channel channel, String keyName,String userId){
        AttributeKey key = null;
        if (AttributeKey.exists(keyName)){//如果已经有了，那就是他
            key = AttributeKey.valueOf(keyName);
        }
        else {//如果没有，那就创建一个名为"userId"的key
            key = AttributeKey.newInstance(keyName);
        }
        channel.attr(key).set(userId);
    }

    public static String getcontext(Channel channel, String key) {
        return channel.attr(AttributeKey.valueOf(key)).get().toString();
    }

}
