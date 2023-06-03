package dku.cloudcomputing.surveyserver.service.query;

import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.entity.survey.*;
import dku.cloudcomputing.surveyserver.repository.member.MemberRepository;
import dku.cloudcomputing.surveyserver.repository.survey.MultipleChoiceOptionRepository;
import dku.cloudcomputing.surveyserver.repository.survey.SurveyDetailRepository;
import dku.cloudcomputing.surveyserver.repository.survey.SurveyRepository;
import dku.cloudcomputing.surveyserver.repository.survey.query.dto.*;
import dku.cloudcomputing.surveyserver.security.JwtAuthenticator;
import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import dku.cloudcomputing.surveyserver.service.survey.query.SurveyQueryService;
import io.github.lazymockbean.annotation.LazyInjectMockBeans;
import io.github.lazymockbean.annotation.LazyMockBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class SurveyQueryServiceTest {
    @LazyMockBean
    JwtAuthenticator jwtAuthenticator;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    SurveyDetailRepository detailRepository;
    @Autowired
    MultipleChoiceOptionRepository optionRepository;

    @LazyInjectMockBeans
    @Autowired
    SurveyQueryService surveyQueryService;

    @Test
    @DisplayName("공개 설문만 조회")
    void queryPublicSurveys() {
        Member savedMember = memberRepository.save(new Member(1L, "test@test", "test", "test"));
        Survey survey1 = createMixedSurveyEntity(savedMember, true);
        Survey survey2 = createMixedSurveyEntity(savedMember, false);
        saveSurvey(survey1);
        saveSurvey(survey2);

        List<SimpleSurveyQueryDto> simpleSurveyQueryDtos = surveyQueryService.queryPublicSurveys(0);

        assertThat(simpleSurveyQueryDtos.size()).isEqualTo(1);
        SimpleSurveyQueryDto simpleSurveyQueryDto = simpleSurveyQueryDtos.get(0);
        assertThat(simpleSurveyQueryDto.getId()).isEqualTo(survey1.getId());
        assertThat(simpleSurveyQueryDto.getName()).isEqualTo(survey1.getName());
        assertThat(simpleSurveyQueryDto.getDuration()).isEqualTo(survey1.getDuration());
    }

    @Test
    @DisplayName("특정 회원 설문만 조회")
    void querySpecificMemberSurveys() {
        when(jwtAuthenticator.getEmail("test2")).thenReturn("test2@test");
        Member savedMember1 = memberRepository.save(new Member(1L, "test1@test", "test", "test"));
        Member savedMember2 = memberRepository.save(new Member(2L, "test2@test", "test", "test"));
        Survey survey1 = createMixedSurveyEntity(savedMember1, true);
        Survey survey2 = createMixedSurveyEntity(savedMember2, false);
        saveSurvey(survey1);
        saveSurvey(survey2);

        List<SimpleSurveyQueryDto> simpleSurveyQueryDtos = surveyQueryService.queryMemberSurveys("test2", 0);

        assertThat(simpleSurveyQueryDtos.size()).isEqualTo(1);
        SimpleSurveyQueryDto simpleSurveyQueryDto = simpleSurveyQueryDtos.get(0);
        assertThat(simpleSurveyQueryDto.getId()).isEqualTo(survey2.getId());
        assertThat(simpleSurveyQueryDto.getName()).isEqualTo(survey2.getName());
        assertThat(simpleSurveyQueryDto.getDuration()).isEqualTo(survey2.getDuration());
    }

    @Test
    @DisplayName("자세한 설문 조회 조회")
    void queryDetailSurvey() {
        Member savedMember = memberRepository.save(new Member(1L, "test1@test", "test", "test"));
        Survey survey = createMixedSurveyEntity(savedMember, true);
        saveSurvey(survey);

        DetailSurveyQueryDto surveyQueryDto = surveyQueryService.queryDetailSurvey(survey.getId());

        assertThat(surveyQueryDto.getName()).isEqualTo(survey.getName());
        List<SurveyDetailQueryDto> surveyDetailQueryDtos = surveyQueryDto.getSurveyDetails();
        List<SurveyDetail> surveyDetails = survey.getSurveyDetails();
        for (int i = 0; i < 5; i++) {
            SurveyDetailQueryDto detailQueryDto = surveyDetailQueryDtos.get(i);
            SurveyDetail surveyDetail = surveyDetails.get(i);
            assertThat(detailQueryDto.getDetailType()).isEqualTo(SurveyDetailType.SUBJECTIVE);
            assertThat(detailQueryDto.getQuestion()).isEqualTo(surveyDetail.getQuestion());
            assertThat(detailQueryDto).isInstanceOf(SubjectiveSurveyDetailQueryDto.class);
        }

        for (int i = 5; i < 10; i++) {
            SurveyDetailQueryDto detailQueryDto = surveyDetailQueryDtos.get(i);
            SurveyDetail surveyDetail = surveyDetails.get(i);
            assertThat(detailQueryDto.getDetailType()).isEqualTo(SurveyDetailType.MULTIPLE_CHOICE);
            assertThat(detailQueryDto.getQuestion()).isEqualTo(surveyDetail.getQuestion());
            assertThat(detailQueryDto).isInstanceOf(MultipleChoiceSurveyDetailQueryDto.class);

            List<MultipleChoiceOptionQueryDto> options = ((MultipleChoiceSurveyDetailQueryDto) detailQueryDto).getOptions();
            List<MultipleChoiceOption> multipleChoiceOptions = ((MultipleChoiceSurveyDetail) surveyDetail).getMultipleChoiceOptions();
            for (int j = 0; j < 5; j++) {
                MultipleChoiceOptionQueryDto multipleChoiceOptionQueryDto = options.get(j);
                MultipleChoiceOption multipleChoiceOption = multipleChoiceOptions.get(j);
                assertThat(multipleChoiceOptionQueryDto.getOption()).isEqualTo(multipleChoiceOption.getOption());
            }
        }
    }

    private void saveSurvey(Survey survey) {
        surveyRepository.save(survey);
        detailRepository.saveAll(survey.getSurveyDetails());

        for (SurveyDetail surveyDetail : survey.getSurveyDetails()) {
            List<MultipleChoiceOption> multipleChoiceOptions = new ArrayList<>();
            if(surveyDetail instanceof MultipleChoiceSurveyDetail) {
                multipleChoiceOptions.addAll(((MultipleChoiceSurveyDetail) surveyDetail).getMultipleChoiceOptions());
            }
            if(!multipleChoiceOptions.isEmpty()) optionRepository.saveAll(multipleChoiceOptions);
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