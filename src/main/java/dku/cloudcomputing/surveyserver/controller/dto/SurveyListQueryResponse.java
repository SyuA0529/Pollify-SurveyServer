package dku.cloudcomputing.surveyserver.controller.dto;

import dku.cloudcomputing.surveyserver.repository.survey.query.dto.SimpleSurveyQueryDto;
import lombok.Getter;

import java.util.List;

@Getter
public class SurveyListQueryResponse {
    private int count;
    private List<SimpleSurveyQueryDto> surveys;

    public SurveyListQueryResponse(int count, List<SimpleSurveyQueryDto> surveys) {
        this.count = count;
        this.surveys = surveys;
    }
}
