package com.ibm.watsonhealth.micromedex.core.vo.healthchecks;

import org.apache.sling.hc.api.Result;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SummaryVO {

    @JsonProperty
    private int total = 0;

    @JsonProperty
    private int ok = 0;

    @JsonProperty
    private int info = 0;

    @JsonProperty
    private int warn = 0;

    @JsonProperty
    private int debug = 0;

    @JsonProperty
    private int critical = 0;

    @JsonProperty
    private int error = 0;

    @JsonProperty
    private String result = "PASS";

    public void addStatus(@NotNull final Result.Status status) {
        if (status == Result.Status.OK) {
            this.incOk();
        } else if (status == Result.Status.INFO) {
            this.incInfo();
        } else if (status == Result.Status.WARN) {
            this.incWarn();
        } else if (status == Result.Status.DEBUG) {
            this.incDebug();
        } else if (status == Result.Status.CRITICAL) {
            this.incCritital();
            this.result = "FAIL";
        } else if (status == Result.Status.HEALTH_CHECK_ERROR) {
            this.incError();
            this.result = "FAIL";
        }
    }

    private void incOk() {
        this.total++;
        this.ok++;
    }

    private void incInfo() {
        this.total++;
        this.info++;
    }

    private void incWarn() {
        this.total++;
        this.warn++;
    }

    private void incDebug() {
        this.total++;
        this.debug++;
    }

    private void incCritital() {
        this.total++;
        this.critical++;
    }

    private void incError() {
        this.total++;
        this.error++;
    }

}

