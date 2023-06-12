package dku.cloudcomputing.surveyserver.controller.dto;

import dku.cloudcomputing.surveyserver.repository.survey.query.dto.SimpleSurveyQueryDto;
import lombok.Getter;

import java.util.List;

@Getter
public class SurveyListQueryResponse {
    private final int totalPageNum;
    private final int count;
    private final List<SimpleSurveyQueryDto> surveys;

    public SurveyListQueryResponse(int totalPageNum, int count, List<SimpleSurveyQueryDto> surveys) {
        this.totalPageNum = totalPageNum;
        this.count = count;
        this.surveys = surveys;
    }
}
