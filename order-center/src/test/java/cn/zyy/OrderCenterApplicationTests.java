package cn.zyy;

import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@SpringBootTest
class OrderCenterApplicationTests {

    @Autowired
    private RabbitTemplate template;

    /**
     * 测试 使用topicExchange方式通过  订单中心 发送信息给 库存中心 和 支付中心
     */
    @Test
    public void sendMessageByTopic(){
       String sendMessageToInv = "订单中心产生商品订单信息，将信息发送给库存中心进行消费库存的更改";
       template.convertAndSend("orderExchange", "order.inventory.reduce", sendMessageToInv);
      System.out.println("订单 to 库存成功 " );

        String sendMessageToPay = "订单中心产生商品订单信息，将信息发送支付中心进行支付";
        template.convertAndSend("orderExchange","order.pay.200", sendMessageToPay );
        System.out.println("订单 to  支付成功 " );

    }
}
