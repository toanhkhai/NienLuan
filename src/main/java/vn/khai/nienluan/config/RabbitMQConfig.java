package vn.khai.nienluan.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue queueA() {
        return new Queue("queueA", true);
    }

    @Bean
    public Queue queueB() {
        return new Queue("queueB", true);
    }

    @Bean
    public Queue queueC() {
        return new Queue("queueC", true);
    }

    // Cấu hình Exchange
    @Bean
    public FanoutExchange fanoutExchange() {

        return new FanoutExchange("fanout-exchange");
    }

    // Cấu hình Binding
    @Bean
    public Binding queueA_Binding() {
        return BindingBuilder
                .bind(queueA())
                .to(fanoutExchange());
    }

    @Bean
    public Binding queueB_Binding() {
        return BindingBuilder
                .bind(queueB())
                .to(fanoutExchange());
    }

    @Bean
    public Binding queueC_Binding() {
        return BindingBuilder
                .bind(queueC())
                .to(fanoutExchange());
    }

    //Message converter cho JSON
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Cấu hình RabbitTemplate để dùng JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}