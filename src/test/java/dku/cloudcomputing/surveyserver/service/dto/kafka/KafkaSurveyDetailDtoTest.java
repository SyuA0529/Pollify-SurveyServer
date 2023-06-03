package dku.cloudcomputing.surveyserver.service.dto.kafka;

import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceOption;
import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceSurveyDetail;
import dku.cloudcomputing.surveyserver.entity.survey.SubjectiveSurveyDetail;
import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import dku.cloudcomputing.surveyserver.service.survey.dto.kafka.KafkaDetailOptionDto;
import dku.cloudcomputing.surveyserver.service.survey.dto.kafka.KafkaSurveyDetailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class KafkaSurveyDetailDtoTest {
    @Test
    @DisplayName("설문 주관식 세부 항목 DTO 변환 성공")
    void convertSubjectiveKafkaSurveyDetailDtoSuccess() {
        SubjectiveSurveyDetail surveyDetail = new SubjectiveSurveyDetail(null, "question");

        KafkaSurveyDetailDto kafkaSurveyDetailDto = new KafkaSurveyDetailDto(surveyDetail);

        assertThat(kafkaSurveyDetailDto.getId()).isEqualTo(surveyDetail.getId());
        assertThat(kafkaSurveyDetailDto.getQuestion()).isEqualTo(surveyDetail.getQuestion());
        assertThat(kafkaSurveyDetailDto.getSurveyDetailType()).isEqualTo(SurveyDetailType.SUBJECTIVE);
        assertThat(kafkaSurveyDetailDto.getOptions().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("설문 객관식 세부 항목 DTO 변환 성공")
    void convertMultipleChoiceKafkaSurveyDetailDtoSuccess() {
        MultipleChoiceSurveyDetail surveyDetail = new MultipleChoiceSurveyDetail(null, "question");
        List<MultipleChoiceOption> multipleChoiceOptions = surveyDetail.getMultipleChoiceOptions();
        for (int i = 0; i < 5; i++) multipleChoiceOptions.add(new MultipleChoiceOption(surveyDetail, "option" + i));

        KafkaSurveyDetailDto kafkaSurveyDetailDto = new KafkaSurveyDetailDto(surveyDetail);

        assertThat(kafkaSurveyDetailDto.getId()).isEqualTo(surveyDetail.getId());
        assertThat(kafkaSurveyDetailDto.getQuestion()).isEqualTo(surveyDetail.getQuestion());
        assertThat(kafkaSurveyDetailDto.getSurveyDetailType()).isEqualTo(SurveyDetailType.MULTIPLE_CHOICE);

        List<KafkaDetailOptionDto> kafkaDetailOptionDtos = kafkaSurveyDetailDto.getOptions();
        for (int i = 0; i < 5; i++)
            assertThat(kafkaDetailOptionDtos.get(i).getOption())
                    .isEqualTo(multipleChoiceOptions.get(i).getOption());
    }
}