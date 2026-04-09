package vn.khai.nienluan.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class worker1 {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RabbitListener(queues = "worker-1-queue")
    public void search(Map<String,String> message){
        String keyword = message.get("keyword");
        String sessionId = message.get("sessionId");



    }
}
