package dku.cloudcomputing.surveyserver.entity.survey;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MultipleChoiceOption {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveydetail_id")
    private SurveyDetail surveyDetail;

    @Column(nullable = false)
    private String option;

    public MultipleChoiceOption(SurveyDetail surveyDetail, String option) {
        this.surveyDetail = surveyDetail;
        this.option = option;
    }
}
