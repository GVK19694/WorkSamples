package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.impl.RedBookDataProcessor;

import lombok.Getter;

@JacksonXmlRootElement(localName = "Gfcs")
public class Gfcs {

    private static final List<String> ALLOWED_ROA_CODES = Arrays.asList("EP", "IC", "ID", "IJ", "IL", "IM", "IN", "IO", "IR", "IT", "IV", "PL", "PT",
      "SC");

    @Getter
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Gfc")
    private final List<Gfc> gfcList;

    public Gfcs(@NotNull final List<Resource> resources, @NotNull final SubstanceType substanceType) {
        this.gfcList = new ArrayList<>();
        List<Resource> gfcResources = new ArrayList<>();
        if (substanceType == SubstanceType.DRUG) {
            for (final Resource gcrResource : resources) {
                gfcResources.addAll(StreamSupport
                  .stream(Spliterators.spliteratorUnknownSize(gcrResource.listChildren(), Spliterator.ORDERED), false)
                  .collect(Collectors.toList()));
            }
        }

        if (substanceType == SubstanceType.SOLUTION) {
            gfcResources = resources;
        }

        ValueMap valueMap;
        for (final Resource gfcResource : gfcResources) {
            valueMap = gfcResource.getValueMap();
            if (valueMap.containsKey(RedBookDataProcessor.PROPERTY_NAME_GFC_ROA_CODE)) {
                if (substanceType == SubstanceType.DRUG && ALLOWED_ROA_CODES.contains(
                  valueMap.get(RedBookDataProcessor.PROPERTY_NAME_GFC_ROA_CODE, String.class))) {
                    this.gfcList.add(new Gfc(gfcResource));
                }
                if (substanceType == SubstanceType.SOLUTION) {
                    this.gfcList.add(new Gfc(gfcResource));
                }
            }
        }
    }

    public void merge(@NotNull final Gfcs gfcs) {
        this.gfcList.addAll(gfcs.gfcList);
    }

}
