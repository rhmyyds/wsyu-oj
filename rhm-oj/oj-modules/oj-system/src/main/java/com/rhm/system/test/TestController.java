package com.rhm.system.test;

import com.rhm.common.redis.service.RedisService;
import com.rhm.system.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ITestService testService;
    @Autowired
    private RedisService redisService;

    @GetMapping("/list")
    public List<?> list(){
        return testService.list();
    }
    @GetMapping("/redis")
    public String redisAddAndGet(){
        redisService.setCacheObject("redis", "rhmyyds");
        return redisService.getCacheObject("redis",String.class);
    }
}
