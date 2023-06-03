package dku.cloudcomputing.surveyserver.controller;

import dku.cloudcomputing.surveyserver.controller.dto.StatusResponseDto;
import dku.cloudcomputing.surveyserver.controller.dto.SurveyListQueryResponse;
import dku.cloudcomputing.surveyserver.exception.dto.FieldBindException;
import dku.cloudcomputing.surveyserver.repository.survey.query.dto.DetailSurveyQueryDto;
import dku.cloudcomputing.surveyserver.repository.survey.query.dto.SimpleSurveyQueryDto;
import dku.cloudcomputing.surveyserver.service.survey.query.SurveyQueryService;
import dku.cloudcomputing.surveyserver.service.survey.dto.controller.CreateSurveyRequestDto;
import dku.cloudcomputing.surveyserver.service.survey.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyQueryService surveyQueryService;
    private final SurveyService surveyService;

    @GetMapping("/surveys")
    public SurveyListQueryResponse queryPublicSurveys(@RequestParam(defaultValue = "0") int page) {
        List<SimpleSurveyQueryDto> simpleSurveyQueryDtos = surveyQueryService.queryPublicSurveys(page);
        return new SurveyListQueryResponse(simpleSurveyQueryDtos.size(), simpleSurveyQueryDtos);
    }

    @GetMapping("/surveys/member")
    public SurveyListQueryResponse queryMemberSurveys(@RequestHeader(value = "Authorization") String token,
                                                      @RequestParam(defaultValue = "0") int page) {
        List<SimpleSurveyQueryDto> simpleSurveyQueryDtos = surveyQueryService.queryMemberSurveys(getParseableToken(token), page);
        return new SurveyListQueryResponse(simpleSurveyQueryDtos.size(), simpleSurveyQueryDtos);
    }

    @GetMapping("/surveys/{surveyId}")
    public DetailSurveyQueryDto querySurveyDetail(@PathVariable Long surveyId) {
        return surveyQueryService.queryDetailSurvey(surveyId);
    }

    @PostMapping("/surveys")
    public StatusResponseDto createSurvey(@RequestHeader(value = "Authorization") String token,
                                          @Validated @RequestBody CreateSurveyRequestDto createSurveyRequestDto,
                                          BindingResult bindingResult) {
        if(bindingResult.hasFieldErrors()) throw new FieldBindException(bindingResult.getFieldErrors());
        surveyService.saveSurvey(getParseableToken(token), createSurveyRequestDto);
        return new StatusResponseDto("success");
    }

    @DeleteMapping("/surveys/{surveyId}")
    public StatusResponseDto deleteSurvey(@RequestHeader(value = "Authorization") String token,
                                          @PathVariable Long surveyId) {
        surveyService.deleteSurvey(getParseableToken(token), surveyId);
        return new StatusResponseDto("success");
    }

    private static String getParseableToken(String token) {
        return token.split(" ")[1].trim();
    }
}
