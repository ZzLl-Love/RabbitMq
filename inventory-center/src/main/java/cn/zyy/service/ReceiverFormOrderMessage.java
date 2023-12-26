package cn.zyy.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 接受订单中心的发动到库存队列中的信息
 */
@Service
@Slf4j
public class ReceiverFormOrderMessage {

        @RabbitListener(queues={"inventoryQueue"})
        public void receiver(String message){
            log.info("***接受订单队列中的信息***" + message);
        }

}
