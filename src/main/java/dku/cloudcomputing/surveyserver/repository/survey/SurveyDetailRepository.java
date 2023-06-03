package dku.cloudcomputing.surveyserver.repository.survey;

import dku.cloudcomputing.surveyserver.entity.survey.SurveyDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyDetailRepository extends JpaRepository<SurveyDetail, Long> {
    @Query("select mcsd.id from MultipleChoiceSurveyDetail mcsd where mcsd.survey.id = :surveyId")
    List<Long> findMultipleSelectDetailIdsBySurveyId(@Param("surveyId") Long surveyId);

    @Modifying
    @Query("delete from SurveyDetail sd where sd.survey.id = :surveyId")
    void deleteBySurveyId(@Param("surveyId") Long surveyId);
}
