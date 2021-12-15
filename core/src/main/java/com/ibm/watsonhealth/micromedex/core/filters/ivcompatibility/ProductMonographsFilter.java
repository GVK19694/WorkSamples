package com.ibm.watsonhealth.micromedex.core.filters.ivcompatibility;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.ibm.watsonhealth.micromedex.core.constants.TemplateConstants;
import com.ibm.watsonhealth.micromedex.core.utils.TemplateUtils;

public class ProductMonographsFilter extends PageFilter {

    @Override
    public boolean includes(final Page page) {
        final boolean result = super.includes(page);
        return result && TemplateUtils.isTemplateType(page, TemplateConstants.IV_COMPATIBILITY_PRODUCT);
    }

}
