package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.ibm.watsonhealth.micromedex.core.enums.citation.CitationType;
import com.ibm.watsonhealth.micromedex.core.models.citation.Citation;

@JacksonXmlRootElement(localName = "Reference")
public class Reference {

    @JacksonXmlProperty(localName = "ProductDataID")
    private long productDataId;

    @JacksonXmlProperty(localName = "Field")
    private String field = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "Code")
    private String code = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "Author")
    private String author = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "Title")
    private String title = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "Journal")
    private String journal = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "Year")
    private String year = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "Volume")
    private String volume = StringUtils.EMPTY;

    @JacksonXmlProperty(localName = "Pages")
    private String pages = StringUtils.EMPTY;

    public Reference(@NotNull final Resource citationComponentResources, @NotNull final String fieldName, final long productId) {
        final Citation citation = citationComponentResources.adaptTo(Citation.class);
        if (citation != null) {
            this.productDataId = productId;
            this.field = fieldName;
            this.code = Long.toString(citation.getCitationId());
            this.year = citation.getPublicationDate();

            this.setAuthor(citation);
            this.setTitle(citation);
            this.setJournal(citation);
            this.setVolume(citation);
            this.setPages(citation);
        }
    }

    private void setAuthor(final Citation citation) {
        if (citation.getCitationType() == CitationType.GENERIC) {
            this.author = citation.getAuthor();
        } else if (citation.getCitationType() == CitationType.BOOK || citation.getCitationType() == CitationType.JOURNAL || citation.getCitationType() == CitationType.ELECTRONIC) {
            this.author = citation.getFirstAuthor();
            if (StringUtils.isNotBlank(citation.getSecondAuthor())) {
                this.author = this.author + ", " + citation.getSecondAuthor();
            }
            if (StringUtils.isNotBlank(citation.getThirdAuthor())) {
                this.author = this.author + ", " + citation.getThirdAuthor();
            }
            if (citation.isEtalEnabled()) {
                this.author = this.author + ", Et al";
            }
        }
    }

    private void setTitle(final Citation citation) {
        if (citation.getCitationType() == CitationType.PRODUCT_INFO) {
            this.title = "Prescribing Information";
        } else if (citation.getCitationType() == CitationType.GENERIC || citation.getCitationType() == CitationType.BOOK || citation.getCitationType() == CitationType.JOURNAL || citation.getCitationType() == CitationType.ELECTRONIC) {
            this.title = citation.getTitle();
        }
    }

    private void setJournal(final Citation citation) {
        if (citation.getCitationType() == CitationType.BOOK || citation.getCitationType() == CitationType.JOURNAL || citation.getCitationType() == CitationType.ELECTRONIC) {
            this.journal = citation.getJournalAbbreviation();
        }
    }

    private void setVolume(final Citation citation) {
        if (citation.getCitationType() == CitationType.BOOK || citation.getCitationType() == CitationType.JOURNAL || citation.getCitationType() == CitationType.ELECTRONIC) {
            this.volume = citation.getVolumeIssue();
        }
    }

    private void setPages(final Citation citation) {
        if (citation.getCitationType() == CitationType.BOOK || citation.getCitationType() == CitationType.JOURNAL || citation.getCitationType() == CitationType.ELECTRONIC) {
            this.pages = citation.getPages();
        }
    }

}
