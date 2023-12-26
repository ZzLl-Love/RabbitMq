package cn.zyy;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class InventoryCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryCenterApplication.class, args);
    }

}
