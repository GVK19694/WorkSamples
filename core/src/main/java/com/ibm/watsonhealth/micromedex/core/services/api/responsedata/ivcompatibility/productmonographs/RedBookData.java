package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;

import lombok.Getter;

@JacksonXmlRootElement(localName = "RedbookData")
public class RedBookData {

    @Getter
    @JacksonXmlProperty(localName = "Gfcs")
    private final Gfcs gfcs;

    @JacksonXmlProperty(isAttribute = true)
    private final boolean derived = true;

    public RedBookData(@NotNull final List<Resource> resources, @NotNull final SubstanceType substanceType) {
        this.gfcs = new Gfcs(resources, substanceType);
    }

    public void merge(@NotNull final RedBookData redBookData) {
        this.gfcs.merge(redBookData.gfcs);
    }

}
