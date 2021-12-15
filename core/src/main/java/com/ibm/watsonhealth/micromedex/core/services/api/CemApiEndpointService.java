package com.ibm.watsonhealth.micromedex.core.services.api;

import java.util.Optional;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;

import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.CantApplyToResponseException;
import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.GetDataException;

public interface CemApiEndpointService {

    String ENDPOINT_TYPE_PROPERTY_NAME = "endpointType";

    Optional<ResponseData> getData(@NotNull final SlingHttpServletRequest request) throws GetDataException;

    void applyToResponse(final ResponseData data, @NotNull final SlingHttpServletResponse response) throws CantApplyToResponseException;

}
