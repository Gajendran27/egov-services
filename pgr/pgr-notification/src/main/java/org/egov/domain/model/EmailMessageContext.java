package org.egov.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class EmailMessageContext {
    private String bodyTemplateName;
    private String subjectTemplateName;
    private Map<Object, Object> bodyTemplateValues;
    private Map<Object, Object> subjectTemplateValues;
}