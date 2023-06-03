package dku.cloudcomputing.surveyserver.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldBindingErrorDto {
    private String field;
    private String reason;
}