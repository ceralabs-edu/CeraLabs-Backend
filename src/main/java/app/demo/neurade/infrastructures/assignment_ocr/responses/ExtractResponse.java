package app.demo.neurade.infrastructures.assignment_ocr.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtractResponse {

    /**
     * key: question_1, question_2...
     * value: list region
     */
    private Map<String, List<QuestionRegionDTO>> questions;

    /**
     * key: question_1 -> A / B / C / D / Đ / S
     * nullable
     */
    @JsonProperty("correct_options")
    private Map<String, String> correctOptions;

    /**
     * key: question_1
     * value: list region
     * nullable
     */
    private Map<String, List<List<Object>>> explains;

    // ================= INNER DTO =================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionRegionDTO {
        private Integer page;

        /**
         * [x1, y1, x2, y2, base64]
         */
        private List<Object> coor;
    }
}