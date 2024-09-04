package com.ychat.api.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("user-service")
public interface UserClient {

//    @PutMapping("/users/money/deduct")
//    void deductMoney(@RequestParam("pw") String pw,@RequestParam("amount") Integer amount);

    @PostMapping("/users/existUsers")
    int existUsers(@RequestBody List<String> userIds);

}