package dku.cloudcomputing.surveyserver.repository.survey.query;

import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceOption;
import dku.cloudcomputing.surveyserver.repository.survey.query.dto.MultipleChoiceOptionQueryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceOptionQueryRepository extends JpaRepository<MultipleChoiceOption, Long> {
    @Query("select new dku.cloudcomputing.surveyserver.repository.survey.query.dto.MultipleChoiceOptionQueryDto(mco.surveyDetail.id, mco.id, mco.option)" +
            " from MultipleChoiceOption mco" +
            " where mco.surveyDetail.id in :surveyDetailIds")
    List<MultipleChoiceOptionQueryDto> findBySurveyDetailIds(@Param("surveyDetailIds") List<Long> surveyDetailIds);
}
