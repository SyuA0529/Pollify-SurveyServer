package dku.cloudcomputing.surveyserver.exception.member;

import dku.cloudcomputing.surveyserver.exception.ClientOccurException;

public class NoSuchMemberException extends ClientOccurException {
    public NoSuchMemberException() {
        new NoSuchMemberException("이메일과 일치하는 회원이 없습니다");
    }

    public NoSuchMemberException(String message) {
        super(message);
    }
}
