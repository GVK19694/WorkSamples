package com.ibm.watsonhealth.micromedex.core.models.citation;

import org.osgi.annotation.versioning.ConsumerType;

import com.ibm.watsonhealth.micromedex.core.enums.citation.CitationType;

@ConsumerType
public interface Citation {

    Long getCitationId();

    CitationType getCitationType();

    boolean isProductInfo();

    boolean isGeneric();

    boolean isBook();

    boolean isJournal();

    boolean isElectronic();

    String getPublicationDate();

    String getCity();

    String getState();

    String getCountry();

    String getComment();

    String getBrandName();

    String getGenericName();

    String getManufacturer();

    String getAuthor();

    String getTitle();

    String getSource();

    String getInline();

    String getCitrefText();

    String getFirstAuthor();

    String getSecondAuthor();

    String getThirdAuthor();

    boolean isEtalEnabled();

    String getVolumeIssue();

    String getPublisherName();

    String getDocumentationType();

    String getPages();

    String getEditors();

    String getEdition();

    String getChapterTitle();

    String getJournalAbbreviation();

    String getUrl();

    String getAccessedOn();

}
