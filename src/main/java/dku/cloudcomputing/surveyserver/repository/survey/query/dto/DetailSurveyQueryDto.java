package dku.cloudcomputing.surveyserver.repository.survey.query.dto;

import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceSurveyDetail;
import dku.cloudcomputing.surveyserver.entity.survey.SubjectiveSurveyDetail;
import dku.cloudcomputing.surveyserver.entity.survey.Survey;
import dku.cloudcomputing.surveyserver.entity.survey.SurveyDetail;
import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DetailSurveyQueryDto {
    private String name;
    private List<SurveyDetailQueryDto> surveyDetails = new ArrayList<>();

    public DetailSurveyQueryDto(Survey survey) {
        this.name = survey.getName();
        List<SurveyDetail> surveyDetails = survey.getSurveyDetails();
        addSurveyDetailQueryDtos(surveyDetails);
    }

    private void addSurveyDetailQueryDtos(List<SurveyDetail> surveyDetails) {
        for (SurveyDetail surveyDetail : surveyDetails) {
            if(surveyDetail instanceof SubjectiveSurveyDetail)
                this.surveyDetails.add(new SubjectiveSurveyDetailQueryDto(
                        surveyDetail.getId(), surveyDetail.getQuestion(), SurveyDetailType.SUBJECTIVE));
            else if(surveyDetail instanceof MultipleChoiceSurveyDetail)
                this.surveyDetails.add(new MultipleChoiceSurveyDetailQueryDto(
                        surveyDetail.getId(), surveyDetail.getQuestion(), SurveyDetailType.MULTIPLE_CHOICE));
        }
    }
}
