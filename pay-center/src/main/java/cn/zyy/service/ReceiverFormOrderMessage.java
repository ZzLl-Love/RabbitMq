package cn.zyy.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


/**
 * 支付中心  接受 订单中心 的消息
 */
@Service
@Slf4j
public class ReceiverFormOrderMessage {

    @RabbitListener(queues={"payQueue"})
    public void pay_receiver_order(String message){
        log.info("***接受订单队列中的信息***" + message);
    }
}
