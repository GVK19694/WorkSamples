package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.records;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;
import com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.Gfc;
import com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData;

import lombok.Getter;

public class RedBookAgent {

    @Getter
    @JacksonXmlProperty(localName = "name")
    private final String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "gfc")
    private final List<RedBookGfc> gfcs;

    public RedBookAgent(@NotNull final RedBookData redBookData, @NotNull final List<String> drugs, final SubstanceType substanceType) {
        this.name = String.join("-", drugs);
        this.gfcs = new ArrayList<>();
        for (final Gfc gfc : redBookData.getGfcs().getGfcList()) {
            this.gfcs.add(new RedBookGfc(gfc.getCode(), this.name, gfc.getProducts().getNdc(), substanceType));
        }
    }

}
