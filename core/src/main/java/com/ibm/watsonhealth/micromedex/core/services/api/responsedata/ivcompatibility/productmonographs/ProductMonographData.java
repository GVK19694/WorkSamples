package com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs;

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
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Formulation;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Substance;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl.ProductMonograph;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl.ProductMonographs;
import com.ibm.watsonhealth.micromedex.core.services.RedBookService;
import com.ibm.watsonhealth.micromedex.core.services.api.ResponseData;
import com.ibm.watsonhealth.micromedex.core.services.api.exceptions.CantApplyToResponseException;
import com.ibm.watsonhealth.micromedex.core.services.api.vo.GlobalDataVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@JacksonXmlRootElement(localName = "dataroot")
public class ProductMonographData implements ResponseData {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ProductData")
    private final List<ProductMonographDataItem> dataItems;

    public ProductMonographData(@NotNull final SlingHttpServletRequest request, @NotNull final RedBookService redBookService) throws RepositoryException {
        final ProductMonographs productMonographs = request.getResource().adaptTo(ProductMonographs.class);
        if (productMonographs != null) {
            this.dataItems = this.convertRecordsToXmlItems(productMonographs, redBookService);
        } else {
            this.dataItems = new ArrayList<>();
        }
    }

    private List<ProductMonographDataItem> convertRecordsToXmlItems(@NotNull final ProductMonographs productMonographs, @NotNull final RedBookService redBookService) throws RepositoryException {
        final List<ProductMonographDataItem> result = new ArrayList<>();
        for (final ProductMonograph productMonographModel : productMonographs.getProductMonographModels()) {
            final GlobalDataVO globalData = new GlobalDataVO(productMonographModel.getLastUpdated(), productMonographModel.getLastUpdatedBy());
            final Substance substance = productMonographModel.getSubstance();
            for (final Formulation formulation : substance.getFormulations()) {
                result.add(new ProductMonographDataItem(globalData, substance, formulation, redBookService));
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
