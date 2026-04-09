package vn.khai.nienluan.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //Cau hinh cac hang doi - tao ra 3 hang doi
    @Bean
    public Queue worker1(){
        return new Queue("worker-1-queue",true);
    }

    @Bean
    public Queue worker2(){
        return new Queue("worker-2-queue",true);
    }

    @Bean
    public Queue worker3(){
        return new Queue("worker-3-queue",true);
    }

    //Cau hinh Exchange
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("fanout-exchange");
    }

    //Cau hinh Binding

    @Bean
    public Binding worker1Binding(){
        return BindingBuilder
                .bind(worker1())
                .to(fanoutExchange());
    }

    @Bean
    public Binding worker2Binding(){
        return BindingBuilder
                .bind(worker2())
                .to(fanoutExchange());
    }

    @Bean
    public Binding worker3Binding(){
        return BindingBuilder
                .bind(worker3())
                .to(fanoutExchange());
    }
}
