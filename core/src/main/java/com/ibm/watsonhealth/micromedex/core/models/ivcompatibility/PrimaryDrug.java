package com.ibm.watsonhealth.micromedex.core.models.ivcompatibility;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.DrugSource;
import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.FormulationType;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.impl.ProductMonograph;

@ConsumerType
public interface PrimaryDrug extends Solution {

    String getCitationPath();

    String getCitationName();

    DrugSource getPrimaryDrugSource();

    boolean isMonographPrimaryDrugSource();

    boolean isNewPrimaryDrugSource();

    String getPrimaryDrugPath();

    String getPrimaryDrugName();

    ProductMonograph getLinkedProductMonograph();

    FormulationType getPrimaryDrugType();

    List<String> getPrimaryDrugs();

    List<String> getAlternativeNames();

    String getManufacturer();

    String getTradeName();

    String getStudyPeriod();

    String getMethod();

    String getContainer();

    String getStorage();

    List<Concentration> getConcentrations();

}
