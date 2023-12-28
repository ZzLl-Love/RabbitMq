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
class DelayedRabbitmqSendApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void test_plugs_delayed(){

        CorrelationData data = new CorrelationData("插件实现延时队列——01");

        MessagePostProcessor processor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {

                MessageProperties properties = message.getMessageProperties();
                properties.setDelay(5000);
                return message ;
            }
        };


        rabbitTemplate.convertAndSend("plugs-delayed-exchange", "ack", "插件实现延时队列5秒",processor,data);
    }
}
