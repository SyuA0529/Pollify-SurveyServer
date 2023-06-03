package dku.cloudcomputing.surveyserver.repository.survey.query;

import dku.cloudcomputing.surveyserver.entity.survey.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyQueryRepository extends JpaRepository<Survey, Long> {

    Page<Survey> findByVisibility(boolean visibility, Pageable pageable);

    Page<Survey> findByMemberId(Long memberId, Pageable pageable);

    @Query("select distinct s from Survey s join fetch s.surveyDetails where s.id = :surveyId")
    Optional<Survey> findDetailSurveyById(@Param("surveyId") Long surveyId);
}
