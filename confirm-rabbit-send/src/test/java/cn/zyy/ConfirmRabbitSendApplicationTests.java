package cn.zyy;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConfirmRabbitSendApplicationTests {


    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 测试RabbitMq 发布确认机制
     */
    @Test
    void testRabbit_confirm() {

        //设置发送的消息对应id
        CorrelationData data = new CorrelationData("1");

        rabbitTemplate.convertAndSend("confirm-exchange",
                "ack",
                "测试rabbitmq的发布确认机制",
                data);
    }

    /**
     * 测试RabbitMq 回退机制   交换机和队列间设置的路由key为ack
     * 我发送数据时使用error_ack，看交换机是否把消息返回给生产者的ReturnCallBack 实现回退机制
     */
    @Test
    void testRabbit_back() {

        //设置发送的消息对应id
        CorrelationData data = new CorrelationData("1");

        rabbitTemplate.convertAndSend("confirm-exchange",
                "error_ack",
                "测试rabbitmq的回退机制",
                data);
    }


    /**
     * 测试RabbitMq 备份交换机
     */
    @Test
    void testRabbit_back_bakups() {

        //设置发送的消息对应id
        CorrelationData data = new CorrelationData("1");

        rabbitTemplate.convertAndSend("confirm-exchange",
                "error_ack",
                "测试rabbitmq的备份交换机",
                data);
    }

}
