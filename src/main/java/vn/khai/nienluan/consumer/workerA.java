package vn.khai.nienluan.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.khai.nienluan.model.BookResponse;
import vn.khai.nienluan.model.SearchRequest;

import java.util.ArrayList;
import java.util.List;

@Component
public class workerA {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "queueA")
    public void processSearch(SearchRequest request) {

        System.out.println("Worker A (OpenLibrary) đang tìm: " + request.getKeyword()
                + " cho session: " + request.getSessionId());

        RestTemplate restTemplate = new RestTemplate();
        String searchKeyword = request.getKeyword().replace(" ", "+");
        // Tăng limit lên 10 để lấy nhiều kết quả
        String apiUrl = "https://openlibrary.org/search.json?q=" + searchKeyword + "&limit=10";

        try {
            String jsonResponse = restTemplate.getForObject(apiUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode docs = root.path("docs");

            List<BookResponse> results = new ArrayList<>();

            if (docs.isArray() && docs.size() > 0) {
                // Lấy tối đa 7 cuốn
                int maxResults = Math.min(docs.size(), 7);

                for (int i = 0; i < maxResults; i++) {
                    JsonNode book = docs.get(i);

                    String title = book.path("title").asText();
                    String author = "Chưa rõ tác giả";
                    if (book.has("author_name") && book.path("author_name").isArray()) {
                        author = book.path("author_name").get(0).asText();
                    }

                    String workKey = book.path("key").asText();
                    String directLink = "https://openlibrary.org" + workKey;

                    String finalTitle = title + " (Tác giả: " + author + ")";
                    BookResponse result = new BookResponse(finalTitle, directLink, "Open Library API");
                    results.add(result);
                }

                // Gửi toàn bộ danh sách kết quả
                String destination = "/topic/results/" + request.getSessionId();
                messagingTemplate.convertAndSend(destination, results);

                System.out.println("✅ Worker A đã gửi " + results.size() + " kết quả đến: " + destination);
                for (BookResponse r : results) {
                    System.out.println("   📖 " + r.getTitle());
                }
            } else {
                System.out.println("Worker A không tìm thấy kết quả cho keyword: " + request.getKeyword());
            }

        } catch (Exception e) {
            System.err.println("Worker A gặp lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}