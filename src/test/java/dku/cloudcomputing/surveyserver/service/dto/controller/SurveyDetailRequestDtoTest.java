package dku.cloudcomputing.surveyserver.service.dto.controller;

import dku.cloudcomputing.surveyserver.entity.survey.*;
import dku.cloudcomputing.surveyserver.exception.dto.CannotConvertToEntityException;
import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.DetailOptionRequestDto;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.SurveyDetailRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


class SurveyDetailRequestDtoTest {
    @Test
    @DisplayName("주관식 설문 세부 항목 DTO 변환 성공")
    void convertSubjectiveSurveyDetailSuccess() {
        SurveyDetailRequestDto surveyDetailRequestDto =
                new SurveyDetailRequestDto("test question", SurveyDetailType.SUBJECTIVE);

        Survey survey = new Survey();
        SurveyDetail surveyDetail = surveyDetailRequestDto.convertToEntity(survey);

        assertThat(surveyDetail.getQuestion())
                .isEqualTo(surveyDetailRequestDto.getQuestion());
        assertThat(surveyDetail).isInstanceOf(SubjectiveSurveyDetail.class);
    }

    @Test
    @DisplayName("겍관식 설문 세부 항목 DTO 변환 성공")
    void convertMultipleChoiceSurveyDetailOptionSuccess() {
        SurveyDetailRequestDto surveyDetailRequestDto =
                new SurveyDetailRequestDto("test question", SurveyDetailType.MULTIPLE_CHOICE);
        List<DetailOptionRequestDto> detailOptionRequestDtos = new ArrayList<>();
        for (int i = 0; i < 5; i++) detailOptionRequestDtos.add(new DetailOptionRequestDto("option" + i));
        surveyDetailRequestDto.setOptions(detailOptionRequestDtos);

        Survey survey = new Survey();
        SurveyDetail surveyDetail = surveyDetailRequestDto.convertToEntity(survey);

        assertThat(surveyDetail.getQuestion()).isEqualTo(surveyDetailRequestDto.getQuestion());
        assertThat(surveyDetail).isInstanceOf(MultipleChoiceSurveyDetail.class);
        List<MultipleChoiceOption> multipleChoiceOptions =
                ((MultipleChoiceSurveyDetail) surveyDetail).getMultipleChoiceOptions();
        for (int i = 0; i < 5; i++) {
            assertThat(multipleChoiceOptions.get(i).getOption())
                    .isEqualTo("option" + i);
        }
    }

    @Test
    @DisplayName("선택지 미존재시 겍관식 설문 세부 항목 DTO 변환 실패")
    void convertMultipleChoiceSurveyDetailOptionFail() {
        SurveyDetailRequestDto surveyDetailRequestDto =
                new SurveyDetailRequestDto("test question", SurveyDetailType.MULTIPLE_CHOICE);

        Survey survey = new Survey();

        assertThatThrownBy(() -> surveyDetailRequestDto.convertToEntity(survey))
                .isInstanceOf(CannotConvertToEntityException.class);
    }
}