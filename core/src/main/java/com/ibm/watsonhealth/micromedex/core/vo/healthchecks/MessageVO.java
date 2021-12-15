package com.ibm.watsonhealth.micromedex.core.vo.healthchecks;

import org.apache.sling.hc.api.ResultLog;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageVO {

    @JsonProperty
    private final String message;

    @JsonProperty
    private final String status;

    public MessageVO(@NotNull final ResultLog.Entry entry) {
        this.message = entry.getMessage();
        this.status = entry.getStatus().name();
    }

}
