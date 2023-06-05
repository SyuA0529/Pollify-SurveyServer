package dku.cloudcomputing.surveyserver.repository.query;

import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.entity.survey.*;
import dku.cloudcomputing.surveyserver.exception.survey.NoSuchSurveyException;
import dku.cloudcomputing.surveyserver.repository.member.MemberRepository;
import dku.cloudcomputing.surveyserver.repository.survey.MultipleChoiceOptionRepository;
import dku.cloudcomputing.surveyserver.repository.survey.SurveyDetailRepository;
import dku.cloudcomputing.surveyserver.repository.survey.SurveyRepository;
import dku.cloudcomputing.surveyserver.repository.survey.query.SurveyQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class SurveyQueryRepositoryTest {
    @Autowired
    private SurveyQueryRepository surveyQueryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyDetailRepository surveyDetailRepository;

    @Autowired
    private MultipleChoiceOptionRepository optionRepository;

    @Test
    @DisplayName("공개된 설문만 조회 가능")
    void findByVisibility() {
        Member savedMember = memberRepository.save(new Member(1L, "test@test", "test", "test"));
        Survey survey1 = createMixedSurveyEntity(savedMember, true);
        Survey survey2 = createMixedSurveyEntity(savedMember, false);
        surveyRepository.save(survey1);
        surveyRepository.save(survey2);

        Page<Survey> findResults = surveyQueryRepository.findByVisibility(true, PageRequest.of(0, 10));
        List<Survey> findSurveys = findResults.getContent();

        assertThat(findSurveys.size()).isEqualTo(1);
        assertThat(findSurveys.get(0).isVisibility()).isTrue();
    }

    @Test
    @DisplayName("회원 아이디로 조회")
    void findByMemberId() {
        Member savedMember1 = memberRepository.save(new Member(1L, "tes1t@test", "test", "test"));
        Member savedMember2 = memberRepository.save(new Member(2L, "test2@test", "test", "test"));
        Survey survey1 = createMixedSurveyEntity(savedMember1, true);
        Survey survey2 = createMixedSurveyEntity(savedMember2, false);
        surveyRepository.save(survey1);
        surveyRepository.save(survey2);

        List<Survey> findSurveys = surveyQueryRepository.findByMemberId(
                savedMember2.getId(), PageRequest.of(0, 10)).getContent();

        assertThat(findSurveys.size()).isEqualTo(1);
        assertThat(findSurveys.get(0).getMember().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("설문 id로 자세한 설문 DTO 조회")
    void findDetailSurveyDtoById() {
        Member savedMember = memberRepository.save(new Member(1L, "tes1t@test", "test", "test"));
        Survey survey = createMixedSurveyEntity(savedMember, true);

        surveyRepository.save(survey);
        for (SurveyDetail surveyDetail : survey.getSurveyDetails()) {
            surveyDetailRepository.save(surveyDetail);
            if(surveyDetail instanceof MultipleChoiceSurveyDetail) {
                optionRepository.saveAll(((MultipleChoiceSurveyDetail) surveyDetail).getMultipleChoiceOptions());
            }
        }

        Survey findSurvey = surveyQueryRepository.findDetailSurveyById(survey.getId())
                .orElseThrow(NoSuchSurveyException::new);

        assertThat(findSurvey.getId()).isEqualTo(survey.getId());
        assertThat(findSurvey.getDuration()).isEqualTo(survey.getDuration());
        assertThat(findSurvey.getStartDate()).isEqualTo(survey.getStartDate());

        List<SurveyDetail> surveyDetails = findSurvey.getSurveyDetails();
        assertThat(surveyDetails.size()).isEqualTo(10);
        for (int i = 0; i < 5; i++) {
            SurveyDetail surveyDetail = surveyDetails.get(i);
            assertThat(surveyDetail).isInstanceOf(SubjectiveSurveyDetail.class);
            assertThat(surveyDetail.getQuestion()).isEqualTo("test" + i);
        }

        for (int i = 5; i < 10; i++) {
            SurveyDetail surveyDetail = surveyDetails.get(i);
            assertThat(surveyDetail).isInstanceOf(MultipleChoiceSurveyDetail.class);
            assertThat(surveyDetail.getQuestion()).isEqualTo("test" + i);
        }
    }

    private static Survey createMixedSurveyEntity(Member member, boolean visibility) {
        Survey survey = new Survey(member, "test", 30, visibility);
        List<SurveyDetail> surveyDetails = survey.getSurveyDetails();

        for (int i = 0; i < 5; i++)
            surveyDetails.add(new SubjectiveSurveyDetail(survey, "test" + i));

        for (int i = 5; i < 10; i++) {
            createMultipleChoiceDetail(survey, surveyDetails, i);
        }
        return survey;
    }

    private static void createMultipleChoiceDetail(Survey survey, List<SurveyDetail> surveyDetails, int i) {
        MultipleChoiceSurveyDetail multipleChoiceSurveyDetail =
                new MultipleChoiceSurveyDetail(survey, "test" + i);
        surveyDetails.add(multipleChoiceSurveyDetail);

        List<MultipleChoiceOption> multipleChoiceOptions = multipleChoiceSurveyDetail.getMultipleChoiceOptions();
        for (int j = 0; j < 5; j++) {
            multipleChoiceOptions.add(new MultipleChoiceOption(multipleChoiceSurveyDetail, "test" + j));
        }
    }
}