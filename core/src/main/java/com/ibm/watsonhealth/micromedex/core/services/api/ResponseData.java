package com.ibm.watsonhealth.micromedex.core.services.api;

import org.apache.sling.api.SlingHttpServletResponse;

import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.CantApplyToResponseException;

public interface ResponseData {

    void applyToResponse(final SlingHttpServletResponse response) throws CantApplyToResponseException;

}
