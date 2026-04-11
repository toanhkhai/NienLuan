package vn.khai.nienluan.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class SearchResponse {
    String nameBook;
    String nameAuthor;
    String url;
    String image;
}
