package dku.cloudcomputing.surveyserver.repository.survey;

import dku.cloudcomputing.surveyserver.entity.survey.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

}
