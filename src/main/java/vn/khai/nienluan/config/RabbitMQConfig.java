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
    public Queue queueA(){
        return new Queue("queueA",true);
    }

    @Bean
    public Queue queueB(){
        return new Queue("queueB",true);
    }

    @Bean
    public Queue queueC(){
        return new Queue("queueC",true);
    }

    //Cau hinh Exchange
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("fanout-exchange");
    }

    //Cau hinh Binding

    @Bean
    public Binding queueA_Binding(){
        return BindingBuilder
                .bind(queueA())
                .to(fanoutExchange());
    }

    @Bean
    public Binding queueB_Binding(){
        return BindingBuilder
                .bind(queueB())
                .to(fanoutExchange());
    }

    @Bean
    public Binding queueC_Binding(){
        return BindingBuilder
                .bind(queueC())
                .to(fanoutExchange());
    }
}
