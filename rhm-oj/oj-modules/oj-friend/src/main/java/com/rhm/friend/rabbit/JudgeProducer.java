package com.rhm.friend.rabbit;

import com.rhm.api.domain.dto.JudgeSubmitDTO;
import com.rhm.common.core.constants.RabbitMQConstants;
import com.rhm.common.core.enums.ResultCode;
import com.rhm.common.security.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JudgeProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 生产者发送消息代码
     * @param judgeSubmitDTO
     */
    public void produceMsg(JudgeSubmitDTO judgeSubmitDTO) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConstants.OJ_WORK_QUEUE, judgeSubmitDTO);
        } catch (Exception e) {
            log.error("⽣产者发送消息异常", e);
            throw new ServiceException(ResultCode.FAILED_RABBIT_PRODUCE);
        }
    }
}
