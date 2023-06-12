package dku.cloudcomputing.surveyserver.service.survey.query.dto;

import dku.cloudcomputing.surveyserver.repository.survey.query.dto.SimpleSurveyQueryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class SurveyListQueryDto {
    private int totalSize;
    private List<SimpleSurveyQueryDto> surveyQueryDtos;
}
