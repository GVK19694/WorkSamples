package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.records;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;

@JacksonXmlRootElement(localName = "RedbookData")
public class RedBookData {

    @JacksonXmlProperty(localName = "primaryAgent")
    private RedBookAgent primaryAgent;

    @JacksonXmlProperty(localName = "secondaryAgent")
    private RedBookAgent secondaryAgent;

    @JacksonXmlProperty(localName = "primarySolution")
    private RedBookAgent primarySolution;

    @JacksonXmlProperty(localName = "secondarySolution")
    private RedBookAgent secondarySolution;

    @JacksonXmlProperty(localName = "solution")
    private RedBookAgent solution;

    @JacksonXmlProperty(isAttribute = true)
    private final boolean derived = true;

    public void addPrimaryDrugRedBookData(final com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData redBookData, @NotNull final List<String> drugNames, final SubstanceType substanceType) {
        if (redBookData == null) {
            this.primaryAgent = null;
        } else {
            this.primaryAgent = new RedBookAgent(redBookData, drugNames, substanceType);
        }
    }

    public void addSecondaryDrugRedBookData(final com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData redBookData, @NotNull final List<String> drugNames, final SubstanceType substanceType) {
        if (redBookData == null) {
            this.secondaryAgent = null;
        } else {
            this.secondaryAgent = new RedBookAgent(redBookData, drugNames, substanceType);
        }
    }

    public void addPrimarySolutionRedBookData(final com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData redBookData, @NotNull final String solutionName) {
        if (redBookData == null) {
            this.primarySolution = null;
        } else {
            this.primarySolution = new RedBookAgent(redBookData, Arrays.asList(solutionName), SubstanceType.SOLUTION);
        }
    }

    public void addSecondarySolutionRedBookData(final com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData redBookData, @NotNull final String solutionName) {
        if (redBookData == null) {
            this.secondarySolution = null;
        } else {
            this.secondarySolution = new RedBookAgent(redBookData, Arrays.asList(solutionName), SubstanceType.SOLUTION);
        }
    }

    public void addSolutionRedBookData(final com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData redBookData, @NotNull final String solutionName) {
        if (redBookData == null) {
            this.solution = null;
        } else {
            this.solution = new RedBookAgent(redBookData, Arrays.asList(solutionName), SubstanceType.SOLUTION);
        }
    }

}

