package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.records;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.CombinationType;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Compatibility;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Concentration;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.PrimaryDrug;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.SecondaryDrug;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl.Record;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl.Records;
import com.ibm.watsonhealth.micromedex.core.services.RedBookService;
import com.ibm.watsonhealth.micromedex.core.services.api.ResponseData;
import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.CantApplyToResponseException;
import com.ibm.watsonhealth.micromedex.core.services.api.vo.GlobalDataVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@JacksonXmlRootElement(localName = "dataroot")
public class RecordData implements ResponseData {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ResultRecord")
    private final List<RecordDataItem> dataItems;

    public RecordData(@NotNull final SlingHttpServletRequest request, @NotNull final RedBookService redBookService) throws RepositoryException {
        final Records records = request.getResource().adaptTo(Records.class);
        if (records != null) {
            this.dataItems = this.convertRecordsToXmlItems(records, redBookService);
        } else {
            this.dataItems = new ArrayList<>();
        }
    }

    private List<RecordDataItem> convertRecordsToXmlItems(@NotNull final Records records, @NotNull final RedBookService redBookService) throws RepositoryException {
        final List<RecordDataItem> result = new ArrayList<>();
        for (final Record recordModel : records.getRecordModels()) {
            final GlobalDataVO globalData = new GlobalDataVO(recordModel.getLastUpdated(), recordModel.getLastUpdatedBy());
            final PrimaryDrug primaryDrug = recordModel.getPrimaryDrug();
            for (final Concentration concentration : primaryDrug.getConcentrations()) {
                for (final Compatibility compatibility : concentration.getCompatibilities()) {
                    if (compatibility.getCombinationTypeEnum() == CombinationType.DRUG_SOLUTION) {
                        result.add(new RecordDataItem(globalData, primaryDrug, concentration, compatibility, null, redBookService));
                    } else {
                        for (final SecondaryDrug secondaryDrug : compatibility.getSecondaryDrugs()) {
                            result.add(new RecordDataItem(globalData, primaryDrug, concentration, compatibility, secondaryDrug, redBookService));
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void applyToResponse(final SlingHttpServletResponse response) throws CantApplyToResponseException {
        try {
            final XmlMapper xmlMapper = new XmlMapper();
            //set 'javax.xml.stream.isRepairingNamespaces' to false because without this property the mapper adds namespaces to attributes (see https://stackoverflow.com/questions/14818134/xmlwriter-extends-attribute-name-with-zdef)
            xmlMapper.getFactory().getXMLOutputFactory().setProperty("javax.xml.stream.isRepairingNamespaces", false);
            final String xml = xmlMapper.writeValueAsString(this);
            response.getWriter().print(xml);
        } catch (final IOException ex) {
            throw new CantApplyToResponseException(ex);
        }
    }

}
