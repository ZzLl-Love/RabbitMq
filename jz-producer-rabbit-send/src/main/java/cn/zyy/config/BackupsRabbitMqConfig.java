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
    private static final String CONFIRM_EXCHANGE_NAME = "confirm-exchange";

    //确认队列名称
    private static final String CONFIRM_QUEUE_NAME = "confirm-queue";

    //备份交换机名称
    private static final String BACKUPS_EXCHANGE_NAME = "backups-exchange";

    //备份队列名称
    private static final String BACKUPS_QUEUE_NAME = "backups-queue";

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
     * 创建确认交换机  给定持久化
     *
     * @return
     */
    @Bean(name = "confirmExchange")
    public DirectExchange confirmExchange() {
        return ExchangeBuilder
                .directExchange(CONFIRM_EXCHANGE_NAME)
                //指定备份交换机和它的名称
                .withArgument("alternate-exchange", BACKUPS_EXCHANGE_NAME)
                .durable(true).build();
    }


    /**
     * 创建确认队列
     *
     * @return
     */
    @Bean(name = "confirmQueue")
    public Queue confirmQueue() {
        return QueueBuilder
                .durable(CONFIRM_QUEUE_NAME)
                .build();
    }


    /**
     * 绑定 确认交换机 和 确认队列 并指定路由key: ack
     */
    @Bean
    public Binding binDingExchangeAndQue() {
        return BindingBuilder
                .bind(confirmQueue())
                .to(confirmExchange())
                .with("ack");

    }


    /**
     * 声明备份交换机
     */
    @Bean
    public FanoutExchange backupsExchange() {

        return new FanoutExchange(BACKUPS_EXCHANGE_NAME);
    }

    /**
     * 声明备份队列
     */
    @Bean
    public Queue backupsQueue() {
        return QueueBuilder
                .durable(BACKUPS_QUEUE_NAME)
                .build();
    }


    /**
     * 绑定备份交换机和备份队列的关系
     */
    @Bean
    public Binding backBinding() {
        return BindingBuilder
                .bind(backupsQueue())
                .to(backupsExchange());
    }
}
