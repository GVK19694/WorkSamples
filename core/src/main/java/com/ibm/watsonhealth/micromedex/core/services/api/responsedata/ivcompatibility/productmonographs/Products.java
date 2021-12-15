package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.RedBookDataProcessor;

import lombok.Getter;

public class Products {

    @Getter
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ndc")
    private final List<String> ndc;

    public Products(@NotNull final List<Resource> productResources) {
        this.ndc = new ArrayList<>();
        ValueMap valueMap;
        for (final Resource productResource : productResources) {
            valueMap = productResource.getValueMap();
            if (valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_PRODUCT_NDC_CODE)) {
                this.ndc.add(valueMap.get(RedBookDataProcessor.PROPERTY_NAME_PRODUCT_NDC_CODE, String.class));
            }
        }
    }

}
