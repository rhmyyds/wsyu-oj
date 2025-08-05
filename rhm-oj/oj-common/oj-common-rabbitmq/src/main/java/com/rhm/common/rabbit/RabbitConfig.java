package com.rhm.common.rabbit;

import com.rhm.common.core.constants.RabbitMQConstants;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * rabbitMQ的相关配置
 */
@Configuration
public class RabbitConfig {
    /**
     * 给rabbitMQ服务器创建队列
     * @return
     */
    @Bean
    public Queue workQueue() {
        return new Queue(RabbitMQConstants.OJ_WORK_QUEUE, true);
    }

    /**
     * 配置消息转换器
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
