package cn.zyy.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    //####################--创建库存中心队列和支付中心队列--##############################
    /**
     * 创建库存订单队列
     * @return
     */
    @Bean
    public Queue inventoryQueue(){
        return new Queue("inventoryQueue",true);
    }


    /**
     * 创建支付中心队列
     * @return
     */

    @Bean
    public Queue payQueue(){
        return new Queue("payQueue", true);
    }


    //####################--创建交换机-##############################
    /**
     * 创建topic交换机
     * @return
     */
    @Bean
    public TopicExchange orderExchange(){
        return new TopicExchange("orderExchange");
    }


    //####################--交换机和队列的绑定--##############################


    /**
     *
     * topic 交换机绑定  库存中心队列
     * @return
     */
    @Bean
    public Binding topicExchangeBindInventoryQueue(){

        return BindingBuilder
                .bind(inventoryQueue()) //绑定队列
                .to(orderExchange())    //绑定交换机
                .with("order.inventory.#");
    }



    /**
     * topicExchange bind  订单中心队列
     */
    @Bean
    public Binding topicExchangeBindPayQueue(){

        return BindingBuilder
                .bind(payQueue())
                .to(orderExchange())
                .with("order.pay.#");
    }

}
