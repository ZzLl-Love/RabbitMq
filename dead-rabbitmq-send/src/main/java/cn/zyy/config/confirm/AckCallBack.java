package cn.zyy.config.confirm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: Zz
 * @Date: 2023/12/26/21:32
 * @Description: 致敬
 */
@Component
@Slf4j
public class AckCallBack implements RabbitTemplate.ConfirmCallback {

    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        //获取消息对应的id
        String id = correlationData != null ? correlationData.getId() : "";

        if (ack) {
            //收到消息
            log.info("确认交换机收到id:{} 消息了", id);
        } else {
            //没有
            log.info("确认交换机没有收到id:{} 消息，原因是：{}", id, cause);
        }
    }
}
