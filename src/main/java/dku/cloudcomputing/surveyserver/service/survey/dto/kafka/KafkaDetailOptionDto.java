package dku.cloudcomputing.surveyserver.service.survey.dto.kafka;

import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceOption;
import lombok.Getter;

@Getter
public class KafkaDetailOptionDto {
    private Long id;
    private String option;

    private KafkaDetailOptionDto(Long id, String option) {
        this.id = id;
        this.option = option;
    }

    public KafkaDetailOptionDto(MultipleChoiceOption multipleChoiceOption) {
        this(multipleChoiceOption.getId(), multipleChoiceOption.getOption());
    }
}
