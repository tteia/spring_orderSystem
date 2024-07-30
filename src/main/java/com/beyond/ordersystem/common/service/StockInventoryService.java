package com.beyond.ordersystem.common.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockInventoryService {
    @Qualifier("3")
    private final RedisTemplate<String, Object> redisTemplate;

    public StockInventoryService(@Qualifier("3") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 상품 등록 시 increaseStock 호출
    public Long increaseStock(Long itemId, int quantity){
        // increment 후 재고량을 return 함.
        return redisTemplate.opsForValue().increment(String.valueOf(itemId), quantity);
    }

    // 주문 등록 시 decreaseStock 호출
    public Long decreaseStock(Long itemId, int quantity){
        Object remains = redisTemplate.opsForValue().get(String.valueOf(itemId));
        int longRemains = Integer.parseInt(remains.toString());
        if(longRemains < quantity){
            return -1L;
        }
        else{
            // 감소(decrement) 후 남아있는 잔량 return 됨.
            return redisTemplate.opsForValue().decrement(String.valueOf(itemId), quantity);
        }
    }

}
