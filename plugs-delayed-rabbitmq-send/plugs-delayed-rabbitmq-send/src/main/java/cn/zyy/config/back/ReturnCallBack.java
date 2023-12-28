package cn.zyy.config.back;

import cn.zyy.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReturnCallBack implements RabbitTemplate.ReturnsCallback {

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("消息:{} 被服务器返回，退回原因:{} ,退回码:{}",
                returnedMessage.getMessage(),
                returnedMessage.getReplyText(),
                returnedMessage.getReplyCode());

        //todo 当交换机无法将信息路由到队列中，就会使用回退机制将消息返回给生产者
        //todo  方式一： 尝试重新调用    方式二： 落库处理
    }

}