package dku.cloudcomputing.surveyserver.service;

import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.entity.survey.*;
import dku.cloudcomputing.surveyserver.repository.survey.MultipleChoiceOptionRepository;
import dku.cloudcomputing.surveyserver.repository.survey.SurveyDetailRepository;
import dku.cloudcomputing.surveyserver.service.survey.SurveyService;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.CreateSurveyRequestDto;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.DetailOptionRequestDto;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.SurveyDetailRequestDto;
import dku.cloudcomputing.surveyserver.service.survey.SurveyDetailType;
import dku.cloudcomputing.surveyserver.exception.member.NoSuchMemberException;
import dku.cloudcomputing.surveyserver.exception.member.NotMatchMemberException;
import dku.cloudcomputing.surveyserver.repository.member.MemberRepository;
import dku.cloudcomputing.surveyserver.repository.survey.SurveyRepository;
import dku.cloudcomputing.surveyserver.security.JwtAuthenticator;
import io.github.lazymockbean.annotation.LazyInjectMockBeans;
import io.github.lazymockbean.annotation.LazyMockBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = { "listeners=PLAINTEXT://localhost:31234", "port=31234" }
)
@Transactional
class SurveyServiceTest {

    @LazyMockBean
    private JwtAuthenticator jwtAuthenticator;

    @Autowired
    @LazyInjectMockBeans
    private SurveyService surveyService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private SurveyDetailRepository detailRepository;
    @Autowired
    private MultipleChoiceOptionRepository optionRepository;

    @Test
    @DisplayName("설문 저장 성공 테스트")
    void saveSurveySuccess() {
        when(jwtAuthenticator.getEmail(any(String.class))).thenReturn("test@test");

        Member savedMember = memberRepository.save(new Member(1L, "test@test", "test", "test"));
        CreateSurveyRequestDto createSurveyRequestDto = getCreateSurveyRequestDto();

        surveyService.saveSurvey("", createSurveyRequestDto);

        Survey findSurvey = surveyRepository.findAll().get(0);
        assertThat(findSurvey.getDuration()).isEqualTo(createSurveyRequestDto.getDuration());
        assertThat(findSurvey.getMember().getId()).isEqualTo(savedMember.getId());
        assertThat(findSurvey.isVisibility()).isTrue();

        List<SurveyDetail> surveyDetails = findSurvey.getSurveyDetails();
        for (int i = 0; i < 5; i++) {
            SurveyDetail surveyDetail = surveyDetails.get(i);
            assertThat(surveyDetail.getQuestion()).isEqualTo("question" + i);
            assertThat(surveyDetail).isInstanceOf(SubjectiveSurveyDetail.class);
        }

        for (int i = 5; i < 10; i++) {
            SurveyDetail surveyDetail = surveyDetails.get(i);
            assertThat(surveyDetail.getQuestion()).isEqualTo("question" + i);
            assertThat(surveyDetail).isInstanceOf(MultipleChoiceSurveyDetail.class);

            List<MultipleChoiceOption> multipleChoiceOptions =
                    ((MultipleChoiceSurveyDetail) surveyDetail).getMultipleChoiceOptions();
            for (int j = 0; j < 5; j++)
                assertThat(multipleChoiceOptions.get(j).getOption()).isEqualTo("test" + j);
        }
    }

    @Test
    @DisplayName("회원 미 존재시 설문 생성 실패")
    void saveSurveyFailWhenMemberNotMatch() {
        when(jwtAuthenticator.getEmail(any(String.class)))
                .thenReturn("notExist@test");

        memberRepository.save(new Member(1L, "test@test", "test", "test"));

        assertThatThrownBy(() -> surveyService.saveSurvey("", getCreateSurveyRequestDto()))
                .isInstanceOf(NoSuchMemberException.class);
    }

    @Test
    @DisplayName("설문 삭제 성공")
    void deleteSurveySuccess() {
        when(jwtAuthenticator.getEmail("test1")).thenReturn("test@test");
        memberRepository.save(new Member(1L, "test@test", "test", "test"));
        Long savedSurveyId = surveyService.saveSurvey("test1", getCreateSurveyRequestDto());

        surveyService.deleteSurvey("test1", savedSurveyId);

        List<Survey> surveys = surveyRepository.findAll();
        List<SurveyDetail> details = detailRepository.findAll();
        List<MultipleChoiceOption> options = optionRepository.findAll();
        assertThat(surveys.isEmpty()).isTrue();
        assertThat(details.isEmpty()).isTrue();
        assertThat(options.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("회원 불일치시 설문 삭제 실패")
    void deleteSurveyFailWhenMemberNotMatch() {
        when(jwtAuthenticator.getEmail("test1")).thenReturn("test@test");
        when(jwtAuthenticator.getEmail("test2")).thenReturn("test2@test");

        memberRepository.save(new Member(1L, "test@test", "test", "test"));
        memberRepository.save(new Member(2L, "test2@test", "test", "test"));
        Long savedSurveyId = surveyService.saveSurvey("test1", getCreateSurveyRequestDto());

        assertThatThrownBy(() -> surveyService.deleteSurvey("test2", savedSurveyId))
                .isInstanceOf(NotMatchMemberException.class);
    }


    private CreateSurveyRequestDto getCreateSurveyRequestDto() {
        CreateSurveyRequestDto createSurveyRequestDto =
                new CreateSurveyRequestDto("test", 30, true);
        List<SurveyDetailRequestDto> surveyDetailRequestDtos = createSurveyRequestDto.getSurveyDetails();

        for (int i = 0; i < 5; i++)
            surveyDetailRequestDtos.add(new SurveyDetailRequestDto("question" + i, SurveyDetailType.SUBJECTIVE));

        for (int i = 5; i < 10; i++){
            SurveyDetailRequestDto multipleOption = new SurveyDetailRequestDto("question" + i, SurveyDetailType.MULTIPLE_CHOICE);
            List<DetailOptionRequestDto> options = multipleOption.getOptions();
            for (int j = 0; j < 5; j++) options.add(new DetailOptionRequestDto("test" + j));
            surveyDetailRequestDtos.add(multipleOption);
        }
        return createSurveyRequestDto;
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