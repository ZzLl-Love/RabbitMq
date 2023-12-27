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
class DeadRabbitmqSendApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 死信机制测试
     */
    @Test
    public void dead_info_test(){
        CorrelationData data = new CorrelationData("dead_1");

//        //发送消息时设置消息过期时间为5秒
//        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
//            @Override
//            public Message postProcessMessage(Message message) throws AmqpException {
//                MessageProperties messageProperties = message.getMessageProperties();
//                messageProperties.setExpiration("5000");
//                return message;
//            }
//        };
        //rabbitTemplate.convertAndSend("test-exchange", "ack", "测试死信交换机",messagePostProcessor,data);
        rabbitTemplate.convertAndSend("test-exchange", "ack", "测试死信交换机",data);
    }

    /**
     * 测试超过普通队列的最大长度时， 消息是否会到死信交换机并路由到死信队列中
     */
    @Test
    public void  test_more_max_length(){

        for (int i = 0; i < 11; i++) {
            CorrelationData data = new CorrelationData(i+"");
            rabbitTemplate.convertAndSend("test-exchange", "ack", "测试死信交换机"+i,data);
        }

    }

    /**
     * 测试超过队列的过期时间， 消息是否会到死信交换机并路由到死信队列中
     */
    @Test
    public void  test_more_message_ttl(){

        CorrelationData data = new CorrelationData("more_ttl");
        rabbitTemplate.convertAndSend("test-exchange", "ack", "超过队列的过期时间",data);

    }
}
