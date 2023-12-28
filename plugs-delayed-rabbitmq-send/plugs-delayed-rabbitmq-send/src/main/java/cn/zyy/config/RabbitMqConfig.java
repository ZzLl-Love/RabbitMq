package cn.zyy.config;

import cn.zyy.config.back.ReturnCallBack;
import cn.zyy.config.confirm.AckCallBack;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @Author: Zz
 * @Date: 2023/12/27/19:54
 * @Description: 致敬
 */
@Configuration
@EnableRabbit

public class RabbitMqConfig{


    //延时交换机
    public static final String PLUGS_EXCHANGE = "plugs-delayed-exchange";

    //延时队列
    public static final String PLUGS_QUEUE_NAME = "plugs-delayed-queue";

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private Integer port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    /**
     * 创建RabbitTemplate
     *
     * @return
     */
    @Bean(name = "backupsRabbitMqTemplate")
    public RabbitTemplate rabbitTemplate(AckCallBack ackCallBack, ReturnCallBack returnCallBack) {
        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory());

        //开启确认回调的支持
        template.setConfirmCallback(ackCallBack);

        //开启回退机制支持
        //template.setMandatory(boolean) true： 交换机无法将信息路由到队列中时，返回给生产者 | false :交换机直接丢弃该条信息
        template.setMandatory(true);
        template.setReturnsCallback(returnCallBack);
        return template;
    }


    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        //创建缓存连接工厂
        CachingConnectionFactory factory = new CachingConnectionFactory();

        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        /**
         * todo 完善SIMPLE  CORRELATED 的区别
         */
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return factory;
    }


    /**
     * 创建插件延时交换机
     */
    @Bean
    public CustomExchange plugsDelayedExchange(){

        HashMap<String, Object> ags = new HashMap<>();
        //指定自定义交换机的类型 | 生产中使用topic模式容易报错
        ags.put("x-delayed-type", "direct");
        return new CustomExchange(PLUGS_EXCHANGE, "x-delayed-message",true,false, ags);
    }

    /**
     * 创建延时队列
     */
    @Bean
    public Queue plugsDelayedQueue(){
        return QueueBuilder.durable(PLUGS_QUEUE_NAME).build();
    }


    /**
     * 绑定延时交换机和延时队列
     */
    @Bean
    public Binding plugsDelayedBinding(){
        return BindingBuilder.bind(plugsDelayedQueue()).to(plugsDelayedExchange()).with("ack").noargs();
    }


}
