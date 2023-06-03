package dku.cloudcomputing.surveyserver.service.dto.controller;

import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceOption;
import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceSurveyDetail;
import dku.cloudcomputing.surveyserver.entity.survey.Survey;
import dku.cloudcomputing.surveyserver.entity.survey.SurveyDetail;
import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.CreateSurveyRequestDto;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.DetailOptionRequestDto;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.SurveyDetailRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class CreateSurveyRequestDtoTest {
    @Test
    @DisplayName("세부 항목 없는 설몬 생성 DTO 변환 성공")
    void convertEmptyDetailSurveyDtoSuccessTest() {
        CreateSurveyRequestDto createSurveyRequestDto =
                new CreateSurveyRequestDto("test", 30, true);
        Member member = new Member(1L, "test@test", "test", "test");

        Survey survey = createSurveyRequestDto.convertToEntity(member);

        assertThat(survey.getMember()).isEqualTo(member);
        assertThat(survey.getDuration()).isEqualTo(30);
        assertThat(survey.isVisibility()).isTrue();
    }

    @Test
    @DisplayName("주관식 세부 항목이 있는 설문 DTO 변환 성공")
    void convertSubjectSurveyDtoSuccessTest() {
        CreateSurveyRequestDto createSurveyRequestDto =
                new CreateSurveyRequestDto("test", 30, true);
        List<SurveyDetailRequestDto> surveyDetailRequestDtos = createSurveyRequestDto.getSurveyDetails();
        for (int i = 0; i < 5; i++)
            surveyDetailRequestDtos.add(new SurveyDetailRequestDto("question" + i, SurveyDetailType.SUBJECTIVE));
        Member member = new Member(1L, "test@test", "test", "test");

        Survey survey = createSurveyRequestDto.convertToEntity(member);

        assertThat(survey.getMember()).isEqualTo(member);
        assertThat(survey.getDuration()).isEqualTo(30);
        assertThat(survey.isVisibility()).isTrue();

        List<SurveyDetail> surveyDetails = survey.getSurveyDetails();
        for (int i = 0; i < 5; i++) {
            assertThat(surveyDetails.get(i).getQuestion())
                    .isEqualTo("question" + i);
        }
    }

    @Test
    @DisplayName("객관식 세부 항목 있는 설문 DTO 변환 성공")
    void convertMultipleOptionSurveyDtoSuccessTest() {
        CreateSurveyRequestDto createSurveyRequestDto =
                new CreateSurveyRequestDto("test", 30, true);
        List<SurveyDetailRequestDto> surveyDetailRequestDtos = createSurveyRequestDto.getSurveyDetails();

        for (int i = 0; i < 5; i++)
            surveyDetailRequestDtos.add(new SurveyDetailRequestDto("question" + i, SurveyDetailType.SUBJECTIVE));

        for (int i = 5; i < 10; i++){
            SurveyDetailRequestDto multipleOption = new SurveyDetailRequestDto("question" + i, SurveyDetailType.MULTIPLE_CHOICE);
            List<DetailOptionRequestDto> options = multipleOption.getOptions();
            for (int j = 0; j < 5; j++) options.add(new DetailOptionRequestDto("test" + j));
            surveyDetailRequestDtos.add(multipleOption);
        }
        Member member = new Member(1L, "test@test", "test", "test");

        Survey survey = createSurveyRequestDto.convertToEntity(member);

        assertThat(survey.getMember()).isEqualTo(member);
        assertThat(survey.getDuration()).isEqualTo(30);
        assertThat(survey.isVisibility()).isTrue();

        List<SurveyDetail> surveyDetails = survey.getSurveyDetails();
        for (int i = 0; i < 5; i++)
            assertThat(surveyDetails.get(i).getQuestion())
                    .isEqualTo("question" + i);

        for (int i = 5; i < 10; i++) {
            SurveyDetail findSurveyDetail = surveyDetails.get(i);
            assertThat(findSurveyDetail).isInstanceOf(MultipleChoiceSurveyDetail.class);
            List<MultipleChoiceOption> findMultipleOptions = ((MultipleChoiceSurveyDetail) findSurveyDetail).getMultipleChoiceOptions();
            for (int j = 0; j < 5; j++)
                assertThat(findMultipleOptions.get(j).getOption())
                        .isEqualTo("test" + j);
        }
    }
}