package vn.khai.nienluan.consumer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import vn.khai.nienluan.model.BookResponse;
import vn.khai.nienluan.model.SearchRequest;
import java.util.ArrayList;
import java.util.List;

@Component
public class workerB {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private static final String BASE_URL = "http://thuvienso.thuviencantho.vn";

    @RabbitListener(queues = "queueB")
    public void processSearch(SearchRequest request) {
        List<BookResponse> results = new ArrayList<>();
        String keyWord = request.getKeyword().replace(" ","+");
        String searchUrl = BASE_URL + "/simple-search?query=" + keyWord;

        try {
            Document doc = Jsoup.connect(searchUrl).timeout(10000).get();
            Elements rows = doc.select("table.table tr");

            rows.stream().skip(1).limit(7).forEach(row -> {
                Elements cols = row.select("td");
                if (cols.size() < 2) return;

                Element link = cols.get(1).selectFirst("a");
                if (link == null) return;

                String title = link.text().trim();
                String author = cols.size() >= 3 ? cols.get(2).text().trim() : "";
                String year = cols.get(0).text().trim();

                // Format nhanh tiêu đề: "Tên sách - Tác giả (Năm)"
                String display = String.format("%s%s%s", title,
                        author.isEmpty() || author.equals("null") ? "" : " - " + author,
                        year.isEmpty() || year.equals("null") ? "" : " (" + year + ")");

                results.add(new BookResponse(display, BASE_URL + link.attr("href"), "Thư viện TP. Cần Thơ"));
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        // Nếu không có kết quả hoặc lỗi, thêm link dự phòng
        if (results.isEmpty()) {
            results.add(new BookResponse("Tìm kiếm \"" + request.getKeyword() + "\" tại Thư viện Cần Thơ",
                    searchUrl, "Thư viện TP. Cần Thơ (Xem toàn bộ)"));
        }
        messagingTemplate.convertAndSend("/topic/results/" + request.getSessionId(), results);
    }
}