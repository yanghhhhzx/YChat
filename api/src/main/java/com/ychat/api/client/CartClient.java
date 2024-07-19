package com.ychat.api.client;



import com.ychat.api.dto.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient("cart-service")
public interface CartClient {

    @DeleteMapping("/carts")
    List<ItemDTO> removeByItemIds(@RequestParam("ids") Collection<Long> ids);
}