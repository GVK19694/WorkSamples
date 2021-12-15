package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.RedBookDataProcessor;

import lombok.Getter;

@JacksonXmlRootElement(localName = "Gfc")
public class Gfc {

    @Getter
    @JacksonXmlProperty(localName = "code")
    private final long code;

    @Getter
    @JacksonXmlProperty(localName = "products")
    private final Products products;

    public Gfc(@NotNull final Resource gfcResource) {
        final ValueMap valueMap = gfcResource.getValueMap();
        this.code = valueMap.get(RedBookDataProcessor.PROPERTY_NAME_GFC_CODE, Long.class);
        final List<Resource> productResources = StreamSupport
          .stream(Spliterators.spliteratorUnknownSize(gfcResource.listChildren(), Spliterator.ORDERED), false)
          .collect(Collectors.toList());
        this.products = new Products(productResources);
    }

}
