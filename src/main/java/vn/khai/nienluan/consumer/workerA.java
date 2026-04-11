package vn.khai.nienluan.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.khai.nienluan.model.BookResponse;
import vn.khai.nienluan.model.SearchRequest;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public class workerA {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "queueA")
    public void processSearch(SearchRequest request) {

        RestTemplate restTemplate = new RestTemplate();

        //Keyword search trên url k được khoản trống, ví dụ cloud computer -> cloud+computer
        String searchKeyword = request.getKeyword().replace(" ", "+");
        //Cho Limit lên 10
        String apiUrl = "https://openlibrary.org/search.json?q=" + searchKeyword + "&limit=10";

        try {
            //Gọi API lấy JSON về từ OpenLibrary
            String jsonResponse = restTemplate.getForObject(apiUrl, String.class);

            //Chuyển đổi Json dạng String sang JsonNode
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            //Lấy ra docs (danh sách những cuốn sách)
            JsonNode docs = root.path("docs");

            List<BookResponse> results = new ArrayList<>();

            if (docs.isArray() && !docs.isEmpty()) {
                //lấy tối đa 7 cuốn sách
                int maxResults = Math.min(docs.size(), 7);
                for (int i = 0; i < maxResults; i++) {
                    JsonNode book = docs.get(i);
                    //Lấy ra title và tác giả.
                    String title = book.path("title").asText();
                    String author = "Chưa rõ tác giả";
                    if (book.has("author_name") && book.path("author_name").isArray()) {
                        author = book.path("author_name").get(0).asText();
                    }
                    //Lấy ra key của cuốn sách
                    String workKey = book.path("key").asText();

                    //url https://openlibrary.org + key cuốn sách -> link cuốn sách.
                    String directLink = "https://openlibrary.org" + workKey;

                    String finalTitle = title + " (Tác giả: " + author + ")";

                    //Dữ liệu phản hồi bao gồm : Tên tác giả + link url sách + nguồn thư viện
                    BookResponse result = new BookResponse(finalTitle, directLink, "Open Library API");
                    results.add(result);
                }

                // Gửi toàn bộ danh sách kết quả cho message -> PHẢI CÓ SESSION ID để gửi đúng client cần nhận
                String destination = "/topic/results/" + request.getSessionId();
                messagingTemplate.convertAndSend(destination, results);
            } else {
                System.out.println("Không tìm thấy kết quả cho keyword: " + request.getKeyword());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}