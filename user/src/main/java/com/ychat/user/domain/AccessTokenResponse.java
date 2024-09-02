package com.ychat.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponse {

    //当被序列化为Json对象时，该字段会被命名为"access_token"
    @JsonProperty("access_token")
    private String accessToken;
 
    public String getAccessToken() {
        return accessToken;
    }
 
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}