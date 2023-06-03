package dku.cloudcomputing.surveyserver.service.survey.dto.controller;

import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceOption;
import dku.cloudcomputing.surveyserver.entity.survey.SurveyDetail;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailOptionRequestDto {
    @NotEmpty
    private String option;

    public DetailOptionRequestDto(String option) {
        this.option = option;
    }

    public MultipleChoiceOption convertToEntity(SurveyDetail surveyDetail) {
        return new MultipleChoiceOption(surveyDetail, option);
    }
}
