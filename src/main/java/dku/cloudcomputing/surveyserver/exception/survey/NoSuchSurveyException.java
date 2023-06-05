package dku.cloudcomputing.surveyserver.exception.survey;

import dku.cloudcomputing.surveyserver.exception.ClientOccurException;

public class NoSuchSurveyException extends ClientOccurException {
    public NoSuchSurveyException() {
        this("해당되는 설문이 없습니다");
    }

    public NoSuchSurveyException(String message) {
        super(message);
    }
}
