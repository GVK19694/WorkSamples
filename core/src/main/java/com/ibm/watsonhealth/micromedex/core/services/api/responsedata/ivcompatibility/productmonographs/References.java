package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;

@JacksonXmlRootElement(localName = "References")
public class References {

    @Getter
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Reference")
    private final List<Reference> referencesList = new ArrayList<>();

    public void addReference(@NotNull final String referencePath, @NotNull final String fieldName, final long productId, @NotNull final ResourceResolver resourceResolver) {
        final Resource resource = resourceResolver.getResource(referencePath);
        if (resource != null) {
            final Resource citationComponentResource = resource.getChild(JcrConstants.JCR_CONTENT + "/root/container/citation");
            if (citationComponentResource != null) {
                this.referencesList.add(new Reference(citationComponentResource, fieldName, productId));
            }
        }
    }

}
