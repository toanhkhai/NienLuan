package vn.khai.nienluan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchRequest {
    private String keyword;
    private String sessionId;
}
