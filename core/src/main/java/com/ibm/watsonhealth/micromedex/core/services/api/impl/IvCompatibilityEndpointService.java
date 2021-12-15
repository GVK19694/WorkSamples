package com.ibm.watsonhealth.micromedex.core.services.api.impl;

import java.util.Optional;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.watsonhealth.micromedex.core.constants.TemplateConstants;
import com.ibm.watsonhealth.micromedex.core.services.RedBookService;
import com.ibm.watsonhealth.micromedex.core.services.api.CemApiEndpointService;
import com.ibm.watsonhealth.micromedex.core.services.api.ResponseData;
import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.CantApplyToResponseException;
import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.GetDataException;
import com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.ProductMonographData;
import com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.records.RecordData;
import com.ibm.watsonhealth.micromedex.core.utils.TemplateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = CemApiEndpointService.class, immediate = true, property = {
  CemApiEndpointService.ENDPOINT_TYPE_PROPERTY_NAME + "=" + IvCompatibilityEndpointService.ENDPOINT_TYPE })
public class IvCompatibilityEndpointService implements CemApiEndpointService {

    public static final String ENDPOINT_TYPE = "ivcompatibility";

    @Reference
    private RedBookService redBookService;

    @Override
    public Optional<ResponseData> getData(@NotNull final SlingHttpServletRequest request) throws GetDataException {
        Optional<ResponseData> result = Optional.empty();
        try {
            final Optional<String> template = TemplateUtils.getTemplate(request);
            if (template.isPresent()) {
                if (StringUtils.equals(template.get(), TemplateConstants.IV_COMPATIBILITY_RECORDS_ROOT)) {
                    result = Optional.of(new RecordData(request, this.redBookService));
                } else if (StringUtils.equals(template.get(), TemplateConstants.IV_COMPATIBILITY_PRODUCTS_ROOT)) {
                    result = Optional.of(new ProductMonographData(request, this.redBookService));
                }
            }
        } catch (final RepositoryException ex) {
            throw new GetDataException(ex);
        }
        return result;
    }

    @Override
    public void applyToResponse(final ResponseData data, final @NotNull SlingHttpServletResponse response) throws CantApplyToResponseException {
        data.applyToResponse(response);
    }

}
