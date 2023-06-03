package dku.cloudcomputing.surveyserver.entity.survey;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SUBJECTIVE")
@NoArgsConstructor
public class SubjectiveSurveyDetail extends SurveyDetail{
    public SubjectiveSurveyDetail(Survey survey, String question) {
        super(survey, question);
    }
}
