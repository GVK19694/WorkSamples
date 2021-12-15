package com.ibm.watsonhealth.micromedex.core.vo.healthchecks;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.hc.api.execution.HealthCheckExecutionResult;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "healthchecks")
public class HealthCheckResultsVO {

    @JsonProperty("summary")
    private final SummaryVO summary;

    @JsonProperty("results")
    private final List<HealthCheckResultVO> result = new ArrayList<>();

    public HealthCheckResultsVO() {
        this.summary = new SummaryVO();
    }

    public void addHealthCheckResult(@NotNull final HealthCheckExecutionResult executionResult) {
        this.summary.addStatus(executionResult.getHealthCheckResult().getStatus());
        this.result.add(new HealthCheckResultVO(executionResult));
    }

}
