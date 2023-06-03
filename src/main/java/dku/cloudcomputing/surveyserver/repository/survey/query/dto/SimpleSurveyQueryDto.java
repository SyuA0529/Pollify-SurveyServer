package dku.cloudcomputing.surveyserver.repository.survey.query.dto;

import dku.cloudcomputing.surveyserver.entity.survey.Survey;
import lombok.Getter;

@Getter
public class SimpleSurveyQueryDto {
    private Long id;
    private String name;
    private int duration;

    public SimpleSurveyQueryDto(Long id, String name, int duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
    }

    public SimpleSurveyQueryDto(Survey survey) {
        this(survey.getId(), survey.getName(), survey.getDuration());
    }
}
