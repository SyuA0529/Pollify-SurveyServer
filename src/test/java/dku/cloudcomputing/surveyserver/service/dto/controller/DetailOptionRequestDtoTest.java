package dku.cloudcomputing.surveyserver.service.dto.controller;

import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceOption;
import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceSurveyDetail;
import dku.cloudcomputing.surveyserver.entity.survey.SurveyDetail;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.DetailOptionRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DetailOptionRequestDtoTest {
    @Test
    @DisplayName("DTO 변환 테스트")
    void convertDtoTest() {
        //given
        DetailOptionRequestDto detailOptionRequestDto = new DetailOptionRequestDto("test option");
        SurveyDetail surveyDetail = new MultipleChoiceSurveyDetail();

        //when
        MultipleChoiceOption multipleChoiceOption = detailOptionRequestDto.convertToEntity(surveyDetail);

        //then
        assertThat(multipleChoiceOption.getOption()).isEqualTo(detailOptionRequestDto.getOption());
    }

}