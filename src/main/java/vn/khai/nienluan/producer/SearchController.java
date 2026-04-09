package vn.khai.nienluan.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.khai.nienluan.model.SearchRequest;

import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/api/search")
    public String search(@RequestBody SearchRequest searchRequest){
        String keyword = searchRequest.getKeyword();
        String sessionId = searchRequest.getSessionId();

        Map<String,String> message = Map.of(
                "keyword",keyword,
                "sessionId",sessionId
        );

        rabbitTemplate.convertAndSend("fanout-exchange","",message);

        return null;
    }

}
