package dku.cloudcomputing.surveyserver.exception.dto;

import dku.cloudcomputing.surveyserver.exception.ClientOccurException;

public class CannotConvertToEntityException extends ClientOccurException {
    public CannotConvertToEntityException() {
        this("엔티티로 변환할 수 없습니다");
    }

    public CannotConvertToEntityException(String message) {
        super(message);
    }
}
