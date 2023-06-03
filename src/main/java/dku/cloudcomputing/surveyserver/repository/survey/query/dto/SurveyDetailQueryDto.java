package dku.cloudcomputing.surveyserver.repository.survey.query.dto;

import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SurveyDetailQueryDto {
    private Long id;
    private String question;
    private SurveyDetailType detailType;

    public SurveyDetailQueryDto(Long id, String question, SurveyDetailType detailType) {
        this.id = id;
        this.question = question;
        this.detailType = detailType;
    }
}
