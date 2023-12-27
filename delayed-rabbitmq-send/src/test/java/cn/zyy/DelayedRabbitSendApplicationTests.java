package cn.zyy;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DelayedRabbitSendApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 测试RabbitMq的延时队列
     */
    @Test
    public void  test_delayed_rabbitmq(){
        CorrelationData data = new CorrelationData("delayed_01");

        //发送消息时设置消息过期时间为5秒
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties messageProperties = message.getMessageProperties();
                messageProperties.setExpiration("5000");
                return message;
            }
        };

        rabbitTemplate.convertAndSend("delayed-exchange", "ack", "超过队列的过期时间",messagePostProcessor,data);

    }

}
