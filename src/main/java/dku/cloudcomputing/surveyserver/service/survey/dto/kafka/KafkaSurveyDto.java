package dku.cloudcomputing.surveyserver.service.survey.dto.kafka;

import dku.cloudcomputing.surveyserver.entity.survey.Survey;
import dku.cloudcomputing.surveyserver.entity.survey.SurveyDetail;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class KafkaSurveyDto {
    private Long id;
    private Long memberId;
    private String name;
    private LocalDate startDate;
    private int duration;
    private boolean visibility;
    private List<KafkaSurveyDetailDto> surveyDetails = new ArrayList<>();

    private KafkaSurveyDto(Long id, Long memberId, String name, LocalDate startDate, int duration, boolean visibility) {
        this.id = id;
        this.name = name;
        this.memberId = memberId;
        this.startDate = startDate;
        this.duration = duration;
        this.visibility = visibility;
    }

    public KafkaSurveyDto(Survey survey) {
        this(
                survey.getId(),
                survey.getMember().getId(),
                survey.getName(),
                survey.getStartDate(),
                survey.getDuration(),
                survey.isVisibility()
        );
        convertSurveyDetailToDto(survey.getSurveyDetails(), getSurveyDetails());
    }

    private void convertSurveyDetailToDto(List<SurveyDetail> surveyDetails, List<KafkaSurveyDetailDto> kafkaSurveyDetailDtos) {
        for (SurveyDetail surveyDetail : surveyDetails) {
            kafkaSurveyDetailDtos.add(new KafkaSurveyDetailDto(surveyDetail));
        }
    }
}
