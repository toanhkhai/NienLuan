package vn.khai.nienluan.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.khai.nienluan.model.SearchRequest;

@RestController
public class SearchController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/api/search")
    public ResponseEntity<String> search(@RequestBody SearchRequest searchRequest) {
        rabbitTemplate.convertAndSend("fanout-exchange", "", searchRequest);

        System.out.println("Đã nhận yêu cầu tìm kiếm: " + searchRequest.getKeyword()
                + " - SessionId: " + searchRequest.getSessionId());

        return ResponseEntity.ok("Đang tìm kiếm: " + searchRequest.getKeyword());
    }
}