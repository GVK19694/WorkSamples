package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.records;

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;

public class RedBookGfc {

    @JacksonXmlProperty(localName = "id")
    private final long id;

    @JacksonXmlProperty(localName = "type")
    private final String type;

    @JacksonXmlProperty(localName = "name")
    private final String name;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ndc")
    private final List<String> ndcs;

    public RedBookGfc(final long id, @NotNull final String name, @NotNull final List<String> ndcs, @NotNull final SubstanceType substanceType) {
        this.id = id;
        if (substanceType == null) {
            this.type = null;
        } else {
            this.type = substanceType.getAemValue().toUpperCase(Locale.getDefault());
        }
        this.name = name;
        this.ndcs = ndcs;
    }

}
