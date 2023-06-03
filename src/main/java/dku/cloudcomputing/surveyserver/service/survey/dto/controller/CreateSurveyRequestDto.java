package dku.cloudcomputing.surveyserver.service.survey.dto.controller;

import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.entity.survey.Survey;
import dku.cloudcomputing.surveyserver.entity.survey.SurveyDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateSurveyRequestDto {
    private String name;
    private int duration;
    private boolean visibility;
    private List<SurveyDetailRequestDto> surveyDetails = new ArrayList<>();

    public CreateSurveyRequestDto(String name, int duration, boolean visibility) {
        this.name = name;
        this.duration = duration;
        this.visibility = visibility;
    }

    public Survey convertToEntity(Member member) {
        Survey survey = new Survey(member, this.getName(), this.getDuration(), this.isVisibility());
        addSurveyDetailsToSurvey(survey);
        return survey;
    }

    private void addSurveyDetailsToSurvey(Survey survey) {
        List<SurveyDetail> surveyDetails = survey.getSurveyDetails();
        for (SurveyDetailRequestDto surveyDetailRequestDto : this.surveyDetails) {
            surveyDetails.add(surveyDetailRequestDto.convertToEntity(survey));
        }
    }
}
