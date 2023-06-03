package dku.cloudcomputing.surveyserver.repository.survey.query.dto;

import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultipleChoiceSurveyDetailQueryDto extends SurveyDetailQueryDto{
    private List<MultipleChoiceOptionQueryDto> options;


    public MultipleChoiceSurveyDetailQueryDto(Long id, String question, SurveyDetailType detailType) {
        super(id, question, detailType);
    }
}
