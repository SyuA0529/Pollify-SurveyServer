package dku.cloudcomputing.surveyserver.service.dto.kafka;

import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceOption;
import dku.cloudcomputing.surveyserver.service.survey.dto.kafka.KafkaDetailOptionDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class KafkaDetailOptionDtoTest {
    @Test
    @DisplayName("설문 객관식 세부 항목 선택 옵션 DTO 변환 성공")
    void convertKafkaDetailOptionDtoSuccess() {
        MultipleChoiceOption multipleChoiceOption = new MultipleChoiceOption(null, "test");

        KafkaDetailOptionDto kafkaDetailOptionDto = new KafkaDetailOptionDto(multipleChoiceOption);

        Assertions.assertThat(kafkaDetailOptionDto.getId()).isEqualTo(multipleChoiceOption.getId());
        Assertions.assertThat(kafkaDetailOptionDto.getOption()).isEqualTo(multipleChoiceOption.getOption());
    }
}