package dku.cloudcomputing.surveyserver.service.dto.kafka;


import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.entity.survey.*;
import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import dku.cloudcomputing.surveyserver.service.survey.dto.kafka.KafkaDetailOptionDto;
import dku.cloudcomputing.surveyserver.service.survey.dto.kafka.KafkaSurveyDetailDto;
import dku.cloudcomputing.surveyserver.service.survey.dto.kafka.KafkaSurveyDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class KafkaSurveyDtoTest {
    @Test
    @DisplayName("설문 DTO 변환 성공")
    void convertMixedSurveyDtoSuccess() {
        Member member = new Member(1L, "test@test", "test", "test");
        Survey survey = new Survey(member, "test", 30, true);
        List<SurveyDetail> surveyDetails = survey.getSurveyDetails();
        for (int i = 0; i < 5; i++) surveyDetails.add(new SubjectiveSurveyDetail(survey, "question" + i));
        for (int i = 5; i < 10; i++) {
            MultipleChoiceSurveyDetail multipleChoiceSurveyDetail = new MultipleChoiceSurveyDetail(survey, "question" + i);
            List<MultipleChoiceOption> multipleChoiceOptions = multipleChoiceSurveyDetail.getMultipleChoiceOptions();
            for (int j = 0; j < 5; j++)
                multipleChoiceOptions.add(new MultipleChoiceOption(multipleChoiceSurveyDetail, "option" + j));
            surveyDetails.add(multipleChoiceSurveyDetail);
        }

        KafkaSurveyDto kafkaSurveyDto = new KafkaSurveyDto(survey);

        assertThat(kafkaSurveyDto.getId()).isEqualTo(survey.getId());
        assertThat(kafkaSurveyDto.getStartDate()).isEqualTo(survey.getStartDate());
        assertThat(kafkaSurveyDto.getDuration()).isEqualTo(survey.getDuration());
        assertThat(kafkaSurveyDto.getMemberId()).isEqualTo(survey.getMember().getId());

        List<KafkaSurveyDetailDto> kafkaSurveyDetailDtos = kafkaSurveyDto.getSurveyDetails();
        assertThat(kafkaSurveyDetailDtos.size()).isEqualTo(surveyDetails.size());
        for (int i = 0; i < 5; i++) {
            KafkaSurveyDetailDto kafkaSurveyDetailDto = kafkaSurveyDetailDtos.get(i);
            SurveyDetail surveyDetail = surveyDetails.get(i);
            assertThat(kafkaSurveyDetailDto.getId()).isEqualTo(surveyDetail.getId());
            assertThat(kafkaSurveyDetailDto.getQuestion()).isEqualTo(surveyDetail.getQuestion());
            assertThat(kafkaSurveyDetailDto.getSurveyDetailType()).isEqualTo(SurveyDetailType.SUBJECTIVE);
        }

        for (int i = 5; i < 10; i++) {
            KafkaSurveyDetailDto kafkaSurveyDetailDto = kafkaSurveyDetailDtos.get(i);
            SurveyDetail surveyDetail = surveyDetails.get(i);
            assertThat(kafkaSurveyDetailDto.getId()).isEqualTo(surveyDetail.getId());
            assertThat(kafkaSurveyDetailDto.getQuestion()).isEqualTo(surveyDetail.getQuestion());
            assertThat(kafkaSurveyDetailDto.getSurveyDetailType()).isEqualTo(SurveyDetailType.MULTIPLE_CHOICE);

            List<KafkaDetailOptionDto> kafkaDetailOptionDtos = kafkaSurveyDetailDto.getOptions();
            List<MultipleChoiceOption> multipleChoiceOptions = ((MultipleChoiceSurveyDetail) surveyDetail).getMultipleChoiceOptions();
            for (int j = 0; j < 5; j++) {
                KafkaDetailOptionDto kafkaDetailOptionDto = kafkaDetailOptionDtos.get(j);
                MultipleChoiceOption multipleChoiceOption = multipleChoiceOptions.get(j);

                assertThat(kafkaDetailOptionDto.getId()).isEqualTo(multipleChoiceOption.getId());
                assertThat(kafkaDetailOptionDto.getOption()).isEqualTo(multipleChoiceOption.getOption());
            }
        }
    }
}