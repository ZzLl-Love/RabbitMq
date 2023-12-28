package cn.zyy.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author: Zz
 * @Date: 2023/12/27/20:13
 * @Description: 致敬
 */

@Service
@Slf4j
public class MessageReceive {

    @RabbitListener(queues = {"plugs-delayed-queue"})
    public void receivePlugsDelayedQueues(String body, Channel channel, Message message) throws IOException {

        try{
            log.info("接收到延时队列中的消息:{}", body);
            //   正常处理完业务  false 表示我们正常接受
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){

            //标识消息是否重复处理
            if(message.getMessageProperties().getRedelivered()){
                //重复处理，拒绝签收 直接丢弃消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }else{
                //首次处理 返回队列 重新发送
                 // 第一个boolean true:拒绝所有的消息  false:反之 |
                // 第二个boolean true: 把消息重返到队列中
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }
}
