package dku.cloudcomputing.surveyserver.repository.survey.query.dto;

import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import lombok.Getter;

@Getter
public class SubjectiveSurveyDetailQueryDto extends SurveyDetailQueryDto{
    public SubjectiveSurveyDetailQueryDto(Long id, String question, SurveyDetailType detailType) {
        super(id, question, detailType);
    }
}
