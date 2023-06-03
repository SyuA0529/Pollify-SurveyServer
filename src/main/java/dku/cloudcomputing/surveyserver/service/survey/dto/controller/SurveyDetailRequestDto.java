package dku.cloudcomputing.surveyserver.service.survey.dto.controller;

import dku.cloudcomputing.surveyserver.entity.survey.*;
import dku.cloudcomputing.surveyserver.exception.dto.CannotConvertToEntityException;
import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SurveyDetailRequestDto {
    private String question;
    private SurveyDetailType surveyDetailType;
    private List<DetailOptionRequestDto> options = new ArrayList<>();

    public SurveyDetailRequestDto(String question, SurveyDetailType surveyDetailType) {
        this.question = question;
        this.surveyDetailType = surveyDetailType;
    }

    public SurveyDetail convertToEntity(Survey survey) {
        if(surveyDetailType == SurveyDetailType.SUBJECTIVE)
            return new SubjectiveSurveyDetail(survey, question);

        else if (surveyDetailType == SurveyDetailType.MULTIPLE_CHOICE) {
            MultipleChoiceSurveyDetail multipleChoiceSurveyDetail = new MultipleChoiceSurveyDetail(survey, question);
            addOptionsToSurveyDetail(multipleChoiceSurveyDetail);
            return multipleChoiceSurveyDetail;
        }

        throw new CannotConvertToEntityException();
    }

    private void addOptionsToSurveyDetail(MultipleChoiceSurveyDetail multipleChoiceSurveyDetail) {
        List<MultipleChoiceOption> multipleChoiceOptions = multipleChoiceSurveyDetail.getMultipleChoiceOptions();
        if(options.isEmpty()) throw new CannotConvertToEntityException();
        for (DetailOptionRequestDto detailOptionRequestDto : options) {
            multipleChoiceOptions.add(detailOptionRequestDto.convertToEntity(multipleChoiceSurveyDetail));
        }
    }
}
