package dku.cloudcomputing.surveyserver.entity.survey;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "surveyType")
@NoArgsConstructor
public abstract class SurveyDetail {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    public SurveyDetail(Survey survey, String question) {
        this.survey = survey;
        this.question = question;
    }
}
