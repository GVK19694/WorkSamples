package com.ibm.watsonhealth.micromedex.core.vo.healthchecks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.hc.api.ResultLog;
import org.apache.sling.hc.api.execution.HealthCheckExecutionResult;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthCheckResultVO {

    @JsonProperty
    private final String name;

    @JsonProperty
    private final String mBeanName;

    @JsonProperty
    private final String status;

    @JsonProperty
    private final List<MessageVO> messages = new ArrayList<>();

    public HealthCheckResultVO(@NotNull final HealthCheckExecutionResult executionResult) {
        this.name = executionResult.getHealthCheckMetadata().getName();
        this.mBeanName = executionResult.getHealthCheckMetadata().getMBeanName();
        this.status = executionResult.getHealthCheckResult().getStatus().name();
        final Iterator<ResultLog.Entry> resultsIterator = executionResult.getHealthCheckResult().iterator();
        while (resultsIterator.hasNext()) {
            this.messages.add(new MessageVO(resultsIterator.next()));
        }
    }

}
