package dku.cloudcomputing.surveyserver.service.survey.query;

import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.entity.survey.Survey;
import dku.cloudcomputing.surveyserver.exception.member.NoSuchMemberException;
import dku.cloudcomputing.surveyserver.exception.survey.NoSuchSurveyException;
import dku.cloudcomputing.surveyserver.repository.member.MemberRepository;
import dku.cloudcomputing.surveyserver.repository.survey.query.MultipleChoiceOptionQueryRepository;
import dku.cloudcomputing.surveyserver.repository.survey.query.SurveyQueryRepository;
import dku.cloudcomputing.surveyserver.repository.survey.query.dto.DetailSurveyQueryDto;
import dku.cloudcomputing.surveyserver.repository.survey.query.dto.MultipleChoiceOptionQueryDto;
import dku.cloudcomputing.surveyserver.repository.survey.query.dto.MultipleChoiceSurveyDetailQueryDto;
import dku.cloudcomputing.surveyserver.repository.survey.query.dto.SimpleSurveyQueryDto;
import dku.cloudcomputing.surveyserver.security.JwtAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyQueryService {
    private final int EachPageLength = 10;
    private final MemberRepository memberRepository;
    private final SurveyQueryRepository surveyQueryRepository;
    private final MultipleChoiceOptionQueryRepository optionQueryRepository;
    private final JwtAuthenticator jwtAuthenticator;

    public List<SimpleSurveyQueryDto> queryPublicSurveys(int page) {
        Page<Survey> findResult =
                surveyQueryRepository.findByVisibility(true, PageRequest.of(page, EachPageLength));
        return convertSurveyListToDto(findResult);
    }

    public List<SimpleSurveyQueryDto> queryMemberSurveys(String token, int page) {
        Member findMember = memberRepository.findByEmail(jwtAuthenticator.getEmail(token))
                .orElseThrow(NoSuchMemberException::new);
        Page<Survey> findResult =
                surveyQueryRepository.findByMemberId(findMember.getId(), PageRequest.of(page, EachPageLength));
        return convertSurveyListToDto(findResult);
    }

    public DetailSurveyQueryDto queryDetailSurvey(Long surveyId) {
        DetailSurveyQueryDto surveyQueryDto =
                new DetailSurveyQueryDto(surveyQueryRepository.findDetailSurveyById(surveyId)
                        .orElseThrow(NoSuchSurveyException::new));

        Map<Long, List<MultipleChoiceOptionQueryDto>> mappedOptionQueryDtos =
                optionQueryRepository.findBySurveyDetailIds(getMultipleChoiceIds(surveyQueryDto)).stream()
                .collect(Collectors.groupingBy(MultipleChoiceOptionQueryDto::getSurveyDetailId));

        surveyQueryDto.getSurveyDetails().stream()
                .filter(o -> o instanceof MultipleChoiceSurveyDetailQueryDto)
                .forEach(o -> ((MultipleChoiceSurveyDetailQueryDto) o).setOptions(mappedOptionQueryDtos.get(o.getId())));

        return surveyQueryDto;
    }

    private static List<Long> getMultipleChoiceIds(DetailSurveyQueryDto surveyQueryDto) {
        List<Long> mappedMultipleChoiceIds = surveyQueryDto.getSurveyDetails().stream()
                .filter(o -> o instanceof MultipleChoiceSurveyDetailQueryDto)
                .map(o -> o.getId())
                .collect(Collectors.toList());
        return mappedMultipleChoiceIds;
    }

    private static List<SimpleSurveyQueryDto> convertSurveyListToDto(Page<Survey> findResult) {
        return findResult.getContent().stream().map(SimpleSurveyQueryDto::new).collect(Collectors.toList());
    }
}
