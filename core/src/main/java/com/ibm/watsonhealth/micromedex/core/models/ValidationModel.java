package com.ibm.watsonhealth.micromedex.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.ibm.watsonhealth.micromedex.core.vo.validation.ValidationErrorVO;

public interface ValidationModel {

    Resource getCurrentResource();

    boolean isValid();

    List<ValidationErrorVO> getErrors();

    List<ValidationErrorVO> getErrorsOfSubcomponents();

}
