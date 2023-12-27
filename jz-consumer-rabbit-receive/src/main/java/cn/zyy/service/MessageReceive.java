package cn.zyy.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class MessageReceive {

    @RabbitListener(queues = "backups-queue" )
    public void receive(String body, Channel channel, Message message) throws IOException {
        try {
            log.info("队列收到消息：" + body);

            //模拟异常
            int ret = 1/0;


            /**
             * 正常处理完业务
             * fasle 表示我们正常接受
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){

            /**
             * 表示消息是否重复处理
             */
            if(message.getMessageProperties().getRedelivered()){
                //拒绝签收 丢弃队列中的消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            } else {
                //返回队列，重新发送 第一个boolean true:拒绝所有的消息  false:反之 | 第二个boolean true: 把消息重返到队列中
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true );
            }

        }
    }
}
