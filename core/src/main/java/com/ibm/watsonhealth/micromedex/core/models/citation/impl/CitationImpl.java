package com.ibm.watsonhealth.micromedex.core.models.citation.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.ibm.watsonhealth.micromedex.core.constants.ResourceTypeConstants;
import com.ibm.watsonhealth.micromedex.core.enums.citation.CitationType;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.citation.Citation;
import com.ibm.watsonhealth.micromedex.core.models.exceptions.InitException;
import com.ibm.watsonhealth.micromedex.core.models.impl.AbstractValidationModel;
import com.ibm.watsonhealth.micromedex.core.services.RandomIdService;
import com.ibm.watsonhealth.micromedex.core.services.exceptions.GenerateIdException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = { Citation.class, ValidationModel.class }, resourceType = {
  CitationImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CitationImpl extends AbstractValidationModel implements Citation {

    static final String RESOURCE_TYPE = ResourceTypeConstants.CITATION;

    private static final String ERROR_MESSAGE_TITLE = "'title' must not be empty";
    private static final String ERROR_MESSAGE_FIRST_AUTHOR = "'first' must not be empty";

    @SuppressWarnings("java:S1075")
    private static final String CITATION_ROOT_PATH = "/content/mdx-cem/citation";
    private static final String CITATION_PROPERTY_NAME_ID = "citationId";
    private static final int CITATION_ID_MIN_VALUE = 10000; //inclusive
    private static final int CITATION_ID_MAX_VALUE = 100000; //exclusive

    @OSGiService
    private RandomIdService randomIdService;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private Long citationId;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String citationType;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String publicationDate;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String cityProductInfo;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String cityGeneric;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String cityBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String cityJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String cityElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String stateProductInfo;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String stateGeneric;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String stateBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String stateJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String stateElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String country;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String comment;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String brandName;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String genericName;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String manufacturer;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String author;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String titleGeneric;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String source;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String inline;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String citrefText;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String firstAuthorBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String firstAuthorJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String firstAuthorElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String secondAuthorBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String secondAuthorJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String secondAuthorElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String thirdAuthorBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String thirdAuthorJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String thirdAuthorElectronic;

    @ValueMapValue
    private boolean etalEnabledBook;

    @ValueMapValue
    private boolean etalEnabledJournal;

    @ValueMapValue
    private boolean etalEnabledElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String titleBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String titleJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String titleElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String volumeIssueBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String volumeIssueJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String volumeIssueElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String publisherNameBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String publisherNameJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String publisherNameElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String documentationTypeBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String documentationTypeJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String documentationTypeElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String pagesBook;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String pagesJournal;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String pagesElectronic;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String editors;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String edition;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String chapterTitle;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String journalAbbreviation;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String url;

    @ValueMapValue
    @Default(values = StringUtils.EMPTY)
    private String accessedOn;

    private CitationType citationTypeEnum = null;
    private String city;
    private String state;
    private String title;
    private String firstAuthor;
    private String secondAuthor;
    private String thirdAuthor;
    private boolean etalEnabled;
    private String volumeIssue;
    private String publisherName;
    private String documentationType;
    private String pages;

    @Override
    public void initBeforeValidation() {
        if (!StringUtils.isBlank(this.citationType)) {
            this.citationTypeEnum = CitationType.getCitationType(this.citationType);
        }
    }

    @Override
    protected void customValidation() {
        this.setValid(true);
        if (StringUtils.isBlank(this.citationType)) {
            this.setValid(false);
            this.addError("'citation type' must not be empty");
        }
        if (StringUtils.isBlank(this.publicationDate)) {
            this.setValid(false);
            this.addError("'publication date' must not be empty");
        }

        this.validateProductInfo();
        this.validateGeneric();
        this.validateBook();
        this.validateJournal();
        this.validateElectronic();
    }

    private void validateProductInfo() {
        if (this.citationTypeEnum == CitationType.PRODUCT_INFO) {
            if (StringUtils.isBlank(this.cityProductInfo)) {
                this.setValid(false);
                this.addError("'city' must not be empty");
            }
            if (StringUtils.isBlank(this.stateProductInfo)) {
                this.setValid(false);
                this.addError("'state' must not be empty");
            }
            if (StringUtils.isBlank(this.brandName)) {
                this.setValid(false);
                this.addError("'brand name' must not be empty");
            }
        }
    }

    private void validateGeneric() {
        if (this.citationTypeEnum == CitationType.GENERIC) {
            if (StringUtils.isBlank(this.titleGeneric)) {
                this.setValid(false);
                this.addError(ERROR_MESSAGE_TITLE);
            }
            if (StringUtils.isBlank(this.source)) {
                this.setValid(false);
                this.addError("'source' must not be empty");
            }
        }
    }

    private void validateBook() {
        if (this.citationTypeEnum == CitationType.BOOK) {
            if (StringUtils.isBlank(this.firstAuthorBook)) {
                this.setValid(false);
                this.addError(ERROR_MESSAGE_FIRST_AUTHOR);
            }
            if (StringUtils.isBlank(this.titleBook)) {
                this.setValid(false);
                this.addError(ERROR_MESSAGE_TITLE);
            }
        }
    }

    private void validateJournal() {
        if (this.citationTypeEnum == CitationType.JOURNAL) {
            if (StringUtils.isBlank(this.firstAuthorJournal)) {
                this.setValid(false);
                this.addError(ERROR_MESSAGE_FIRST_AUTHOR);
            }
            if (StringUtils.isBlank(this.titleJournal)) {
                this.setValid(false);
                this.addError(ERROR_MESSAGE_TITLE);
            }
            if (StringUtils.isBlank(this.journalAbbreviation)) {
                this.setValid(false);
                this.addError("'journal abbreviation' must not be empty");
            }
        }
    }

    private void validateElectronic() {
        if (this.citationTypeEnum == CitationType.ELECTRONIC) {
            if (StringUtils.isBlank(this.firstAuthorElectronic)) {
                this.setValid(false);
                this.addError(ERROR_MESSAGE_FIRST_AUTHOR);
            }
            if (StringUtils.isBlank(this.titleElectronic)) {
                this.setValid(false);
                this.addError(ERROR_MESSAGE_TITLE);
            }
            if (StringUtils.isBlank(this.url)) {
                this.setValid(false);
                this.addError("'url' must not be empty");
            }
            if (StringUtils.isBlank(this.accessedOn)) {
                this.setValid(false);
                this.addError("'accessed on' must not be empty");
            }
        }
    }

    @Override
    public void init() throws InitException {
        try {
            this.generateCitationId();
            if (this.citationTypeEnum == CitationType.PRODUCT_INFO) {
                this.city = this.cityProductInfo;
                this.state = this.stateProductInfo;
                this.title = StringUtils.EMPTY;
                this.firstAuthor = StringUtils.EMPTY;
                this.secondAuthor = StringUtils.EMPTY;
                this.thirdAuthor = StringUtils.EMPTY;
                this.etalEnabled = false;
                this.volumeIssue = StringUtils.EMPTY;
                this.publisherName = StringUtils.EMPTY;
                this.documentationType = StringUtils.EMPTY;
                this.pages = StringUtils.EMPTY;
            } else if (this.citationTypeEnum == CitationType.GENERIC) {
                this.city = this.cityGeneric;
                this.state = this.stateGeneric;
                this.title = this.titleGeneric;
                this.firstAuthor = StringUtils.EMPTY;
                this.secondAuthor = StringUtils.EMPTY;
                this.thirdAuthor = StringUtils.EMPTY;
                this.etalEnabled = false;
                this.volumeIssue = StringUtils.EMPTY;
                this.publisherName = StringUtils.EMPTY;
                this.documentationType = StringUtils.EMPTY;
                this.pages = StringUtils.EMPTY;
            } else if (this.citationTypeEnum == CitationType.BOOK) {
                this.city = this.cityBook;
                this.state = this.stateBook;
                this.firstAuthor = this.firstAuthorBook;
                this.secondAuthor = this.secondAuthorBook;
                this.thirdAuthor = this.thirdAuthorBook;
                this.etalEnabled = this.etalEnabledBook;
                this.title = this.titleBook;
                this.volumeIssue = this.volumeIssueBook;
                this.publisherName = this.publisherNameBook;
                this.documentationType = this.documentationTypeBook;
                this.pages = this.pagesBook;
            } else if (this.citationTypeEnum == CitationType.JOURNAL) {
                this.city = this.cityJournal;
                this.state = this.stateJournal;
                this.firstAuthor = this.firstAuthorJournal;
                this.secondAuthor = this.secondAuthorJournal;
                this.thirdAuthor = this.thirdAuthorJournal;
                this.etalEnabled = this.etalEnabledJournal;
                this.title = this.titleJournal;
                this.volumeIssue = this.volumeIssueJournal;
                this.publisherName = this.publisherNameJournal;
                this.documentationType = this.documentationTypeJournal;
                this.pages = this.pagesJournal;
            } else if (this.citationTypeEnum == CitationType.ELECTRONIC) {
                this.city = this.cityElectronic;
                this.state = this.stateElectronic;
                this.firstAuthor = this.firstAuthorElectronic;
                this.secondAuthor = this.secondAuthorElectronic;
                this.thirdAuthor = this.thirdAuthorElectronic;
                this.etalEnabled = this.etalEnabledElectronic;
                this.title = this.titleElectronic;
                this.volumeIssue = this.volumeIssueElectronic;
                this.publisherName = this.publisherNameElectronic;
                this.documentationType = this.documentationTypeElectronic;
                this.pages = this.pagesElectronic;
            } else {
                this.city = StringUtils.EMPTY;
                this.state = StringUtils.EMPTY;
                this.title = StringUtils.EMPTY;
                this.firstAuthor = StringUtils.EMPTY;
                this.secondAuthor = StringUtils.EMPTY;
                this.thirdAuthor = StringUtils.EMPTY;
                this.etalEnabled = false;
                this.volumeIssue = StringUtils.EMPTY;
                this.publisherName = StringUtils.EMPTY;
                this.documentationType = StringUtils.EMPTY;
                this.pages = StringUtils.EMPTY;
            }
        } catch (final GenerateIdException ex) {
            throw new InitException(ex);
        }
    }

    private void generateCitationId() throws GenerateIdException {
        if (this.getCurrentResource() != null && this.citationId == 0) {
            final int generatedId = this.randomIdService.saveAndGetId(CITATION_ID_MIN_VALUE, CITATION_ID_MAX_VALUE, CITATION_ROOT_PATH,
              CITATION_PROPERTY_NAME_ID, ResourceTypeConstants.CITATION, this.getCurrentResource());
            this.citationId = (long) generatedId;
        }
    }

    @Override
    public Long getCitationId() {
        return this.citationId;
    }

    @Override
    public CitationType getCitationType() {
        return this.citationTypeEnum;
    }

    @Override
    public boolean isProductInfo() {
        return this.citationTypeEnum == CitationType.PRODUCT_INFO;
    }

    @Override
    public boolean isGeneric() {
        return this.citationTypeEnum == CitationType.GENERIC;
    }

    @Override
    public boolean isBook() {
        return this.citationTypeEnum == CitationType.BOOK;
    }

    @Override
    public boolean isJournal() {
        return this.citationTypeEnum == CitationType.JOURNAL;
    }

    @Override
    public boolean isElectronic() {
        return this.citationTypeEnum == CitationType.ELECTRONIC;
    }

    @Override
    public String getPublicationDate() {
        return this.publicationDate;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public String getState() {
        return this.state;
    }

    @Override
    public String getCountry() {
        return this.country;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public String getBrandName() {
        return this.citationTypeEnum == CitationType.PRODUCT_INFO ? this.brandName : StringUtils.EMPTY;
    }

    @Override
    public String getGenericName() {
        return this.citationTypeEnum == CitationType.PRODUCT_INFO ? this.genericName : StringUtils.EMPTY;
    }

    @Override
    public String getManufacturer() {
        return this.citationTypeEnum == CitationType.PRODUCT_INFO ? this.manufacturer : StringUtils.EMPTY;
    }

    @Override
    public String getAuthor() {
        return this.citationTypeEnum == CitationType.GENERIC ? this.author : StringUtils.EMPTY;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getSource() {
        return this.citationTypeEnum == CitationType.GENERIC ? this.source : StringUtils.EMPTY;
    }

    @Override
    public String getInline() {
        return this.citationTypeEnum == CitationType.GENERIC ? this.inline : StringUtils.EMPTY;
    }

    @Override
    public String getCitrefText() {
        return this.citationTypeEnum == CitationType.GENERIC ? this.citrefText : StringUtils.EMPTY;
    }

    @Override
    public String getFirstAuthor() {
        return this.firstAuthor;
    }

    @Override
    public String getSecondAuthor() {
        return this.secondAuthor;
    }

    @Override
    public String getThirdAuthor() {
        return this.thirdAuthor;
    }

    @Override
    public boolean isEtalEnabled() {
        return this.etalEnabled;
    }

    @Override
    public String getVolumeIssue() {
        return this.volumeIssue;
    }

    @Override
    public String getPublisherName() {
        return this.publisherName;
    }

    @Override
    public String getDocumentationType() {
        return this.documentationType;
    }

    @Override
    public String getPages() {
        return this.pages;
    }

    @Override
    public String getEditors() {
        return this.citationTypeEnum == CitationType.BOOK ? this.editors : StringUtils.EMPTY;
    }

    @Override
    public String getEdition() {
        return this.citationTypeEnum == CitationType.BOOK ? this.edition : StringUtils.EMPTY;
    }

    @Override
    public String getChapterTitle() {
        return this.citationTypeEnum == CitationType.BOOK ? this.chapterTitle : StringUtils.EMPTY;
    }

    @Override
    public String getJournalAbbreviation() {
        return this.citationTypeEnum == CitationType.JOURNAL ? this.journalAbbreviation : StringUtils.EMPTY;
    }

    @Override
    public String getUrl() {
        return this.citationTypeEnum == CitationType.ELECTRONIC ? this.url : StringUtils.EMPTY;
    }

    @Override
    public String getAccessedOn() {
        return this.citationTypeEnum == CitationType.ELECTRONIC ? this.accessedOn : StringUtils.EMPTY;
    }

}
