package cn.zyy;

import cn.zyy.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class DelayedRabbitmqSendApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DelayedRabbitmqSendApplication.class, args);
        log.info("Active Profiles: {}", Arrays.toString(context.getEnvironment().getActiveProfiles()));
    }

}
