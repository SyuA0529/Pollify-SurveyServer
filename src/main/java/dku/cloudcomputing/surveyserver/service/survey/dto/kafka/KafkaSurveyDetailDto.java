package dku.cloudcomputing.surveyserver.service.survey.dto.kafka;

import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceOption;
import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceSurveyDetail;
import dku.cloudcomputing.surveyserver.entity.survey.SubjectiveSurveyDetail;
import dku.cloudcomputing.surveyserver.entity.survey.SurveyDetail;
import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class KafkaSurveyDetailDto {
    private Long id;
    private String question;
    private SurveyDetailType surveyDetailType;
    private List<KafkaDetailOptionDto> options = new ArrayList<>();

    private KafkaSurveyDetailDto(Long id, String question, SurveyDetailType surveyDetailType) {
        this.id = id;
        this.question = question;
        this.surveyDetailType = surveyDetailType;
    }

    public KafkaSurveyDetailDto(SurveyDetail surveyDetail) {
        this(
                surveyDetail.getId(),
                surveyDetail.getQuestion(),
                surveyDetail instanceof SubjectiveSurveyDetail ?
                        SurveyDetailType.SUBJECTIVE : SurveyDetailType.MULTIPLE_CHOICE
        );
        if(getSurveyDetailType().equals(SurveyDetailType.MULTIPLE_CHOICE)) {
            convertMultipleChoiceOptionToDto(
                    ((MultipleChoiceSurveyDetail) surveyDetail).getMultipleChoiceOptions(),
                    getOptions());
        }
    }

    private void convertMultipleChoiceOptionToDto(List<MultipleChoiceOption> multipleChoiceOptions, List<KafkaDetailOptionDto> detailOptionDtos) {
        for (MultipleChoiceOption multipleChoiceOption : multipleChoiceOptions) {
            detailOptionDtos.add(new KafkaDetailOptionDto(multipleChoiceOption));
        }
    }
}
