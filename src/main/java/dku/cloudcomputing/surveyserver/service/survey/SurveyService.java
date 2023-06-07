package dku.cloudcomputing.surveyserver.service.survey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceOption;
import dku.cloudcomputing.surveyserver.entity.survey.MultipleChoiceSurveyDetail;
import dku.cloudcomputing.surveyserver.entity.survey.Survey;
import dku.cloudcomputing.surveyserver.entity.survey.SurveyDetail;
import dku.cloudcomputing.surveyserver.exception.member.NoSuchMemberException;
import dku.cloudcomputing.surveyserver.exception.member.NotMatchMemberException;
import dku.cloudcomputing.surveyserver.exception.survey.NoSuchSurveyException;
import dku.cloudcomputing.surveyserver.repository.member.MemberRepository;
import dku.cloudcomputing.surveyserver.repository.survey.MultipleChoiceOptionRepository;
import dku.cloudcomputing.surveyserver.repository.survey.SurveyDetailRepository;
import dku.cloudcomputing.surveyserver.repository.survey.SurveyRepository;
import dku.cloudcomputing.surveyserver.security.JwtAuthenticator;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.CreateSurveyRequestDto;
import dku.cloudcomputing.surveyserver.service.survey.dto.kafka.KafkaSurveyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {
    @Value(value = "${message.topic.saveSurvey}")
    private String createSurveyTopic;

    @Value(value = "${message.topic.deleteSurvey}")
    private String deleteSurveyTopic;

    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyDetailRepository detailRepository;
    private final MultipleChoiceOptionRepository optionRepository;

    private final JwtAuthenticator jwtAuthenticator;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor = {Exception.class})
    public Long saveSurvey(String token, CreateSurveyRequestDto createSurveyRequestDto) {
        Member requestMember = memberRepository.findByEmail(jwtAuthenticator.getEmail(token))
                .orElseThrow(NoSuchMemberException::new);

        Survey survey = createSurveyRequestDto.convertToEntity(requestMember);
        surveyRepository.save(survey);
        detailRepository.saveAll(survey.getSurveyDetails());
        optionRepository.saveAll(getMultipleChoiceOptionList(survey));

        try {
            sendCreatedSurveyToKafka(survey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return survey.getId();
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteSurvey(String token, Long surveyId) {
        validateReqMemberEqToCreateMember(token, surveyId);
        if(!surveyRepository.existsById(surveyId)) throw new NoSuchSurveyException();
        sendDeleteSurveyToKafka(surveyId);

        optionRepository.deleteByDetailIdList(detailRepository.findMultipleSelectDetailIdsBySurveyId(surveyId));
        detailRepository.deleteBySurveyId(surveyId);
        surveyRepository.deleteById(surveyId);
    }

    private void validateReqMemberEqToCreateMember(String token, Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(NoSuchSurveyException::new);

        if(!survey.getMember().getEmail().equals(jwtAuthenticator.getEmail(token)))
            throw new NotMatchMemberException();
    }

    private List<MultipleChoiceOption> getMultipleChoiceOptionList(Survey survey) {
        List<MultipleChoiceOption> multipleChoiceOptions = new ArrayList<>();
        for (SurveyDetail surveyDetail : survey.getSurveyDetails()) {
            if(surveyDetail instanceof MultipleChoiceSurveyDetail)
                multipleChoiceOptions.addAll(((MultipleChoiceSurveyDetail) surveyDetail).getMultipleChoiceOptions());
        }
        return multipleChoiceOptions;
    }

    private void sendCreatedSurveyToKafka(Survey savedSurvey) throws JsonProcessingException {
        String kafkaSurveyJson = objectMapper.writeValueAsString(new KafkaSurveyDto(savedSurvey));
        kafkaTemplate.send(createSurveyTopic, kafkaSurveyJson);
    }

    private void sendDeleteSurveyToKafka(long surveyId) {
        try {
            kafkaTemplate.send(deleteSurveyTopic, String.valueOf(surveyId));
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
