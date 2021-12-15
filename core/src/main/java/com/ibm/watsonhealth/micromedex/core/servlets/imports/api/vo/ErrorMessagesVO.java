package com.ibm.watsonhealth.micromedex.core.servlets.imports.api.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessagesVO {

    @JsonProperty("errormessages")
    private final List<ErrorMessageVO> errormessages = new ArrayList<>();

    public ErrorMessagesVO() {
    }

    @JsonCreator
    public ErrorMessagesVO(@JsonProperty("errormessages") final List<ErrorMessageVO> errormessages) {
        if (errormessages != null) {
            this.errormessages.addAll(errormessages);
        }
    }

    public List<ErrorMessageVO> getErrormessages() {
        return this.errormessages;
    }

    public void addMessage(final ErrorMessageVO errormessage) {
        if (errormessage != null) {
            this.errormessages.add(errormessage);
        }
    }

}
