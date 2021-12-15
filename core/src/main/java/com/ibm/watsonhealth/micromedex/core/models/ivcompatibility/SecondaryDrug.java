package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility;

import java.util.List;

import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.DrugSource;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.FormulationType;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl.ProductMonograph;

public interface SecondaryDrug extends Solution {

    long getRecordId();

    DrugSource getSecondaryDrugSource();

    boolean isMonographSecondaryDrugSource();

    boolean isNewSecondaryDrugSource();

    String getSecondaryDrugPath();

    String getSecondaryDrugName();

    ProductMonograph getLinkedProductMonograph();

    FormulationType getSecondaryDrugType();

    List<String> getSecondaryDrugs();

    List<String> getAlternativeNames();

    String getSecondaryDrugConcentration();

    String getManufacturer();

    String getTradeName();

    String getStudyPeriod();

    String getMethod();

    String getContainer();

    String getStorage();

}
