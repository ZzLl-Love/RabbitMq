package cn.zyy.config;

import cn.zyy.config.back.ReturnCallBack;
import cn.zyy.config.confirm.AckCallBack;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Zz
 * @Date: 2023/12/26/22:49
 * @Description: 致敬
 * <p>
 * 备份交换机的配置类
 */
@Configuration
@EnableRabbit
public class BackupsRabbitMqConfig {

    //确认交换机名称
    private static final String TEST_EXCHANGE_NAME = "test-exchange";

    //确认队列名称
    private static final String TEST_QUEUE_NAME = "test-queue";

    //死信交换机名称
    private static final String DEAD_EXCHANGE_NAME = "dead-exchange";

    //死信队列名称
    private static final String DEAD_QUEUE_NAME = "dead-queue";


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
     * 创建TEST交换机  给定持久化
     *
     * @return
     */
    @Bean(name = "testExchange")
    public DirectExchange testExchange() {
        return ExchangeBuilder
                .directExchange(TEST_EXCHANGE_NAME)
                .durable(true).build();
    }


    /**
     * 创TEST队列
     *
     * @return
     */
    @Bean(name = "testQueue")
    public Queue testQueue() {

        Map<String, Object> map = new HashMap<>();
        //完成【普通队列】和 【死信交换机】 的绑定
        map.put("x-dead-letter-exchange",DEAD_EXCHANGE_NAME);

        //限制队列的最大长度
        map.put("x-max-length",10);

        //设置队列的过期时间为5秒
        map.put("x-message-ttl",5000);

        return QueueBuilder
                .durable(TEST_QUEUE_NAME)

                .withArguments(map)
                .build();
    }


    /**
     * 绑定 确认交换机 和 确认队列 并指定路由key: ack
     */
    @Bean
    public Binding binDingExchangeAndQue() {
        return BindingBuilder
                .bind(testQueue())
                .to(testExchange())
                .with("ack");
    }

    /**
     * 声明死信交换机
     */
    @Bean(name="deadExchange")
    public DirectExchange deadExchange(){
        return new DirectExchange(DEAD_EXCHANGE_NAME);
    }


    /**
     *声明死信队列
     */
    @Bean
    public Queue deadQueue(){
        return  QueueBuilder
                .durable(DEAD_QUEUE_NAME)
                .build();
    }

    /**
     * 声明死信交换机和死信队列的绑定
     */
    @Bean
    public Binding deadBind(){
        return BindingBuilder
                .bind(deadQueue())
                .to(deadExchange())
                .with("ack");
    }
}
