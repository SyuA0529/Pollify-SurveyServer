package dku.cloudcomputing.surveyserver.repository.survey.query.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public class MultipleChoiceOptionQueryDto {
    @JsonIgnore
    private Long surveyDetailId;
    private Long optionId;
    private String option;

    public MultipleChoiceOptionQueryDto(Long surveyDetailId, Long optionId, String option) {
        this.surveyDetailId = surveyDetailId;
        this.optionId = optionId;
        this.option = option;
    }
}
