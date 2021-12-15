package com.ibm.watsonhealth.micromedex.core.context.mocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import com.ibm.watsonhealth.micromedex.core.enums.ivcompatibility.SubstanceType;
import com.ibm.watsonhealth.micromedex.core.models.ivcompatibility.Substance;
import com.ibm.watsonhealth.micromedex.core.services.RedBookService;
import com.ibm.watsonhealth.micromedex.core.services.api.responsedata.ivcompatibility.productmonographs.RedBookData;

public class MockRedBookService implements RedBookService {

    @Override
    public Optional<RedBookData> getRedBookData(final @NotNull Substance substance) {
        if (StringUtils.equals("/content/mdx-cem/iv-compatibility/product-monographs/unit-tests/product-1/jcr:content/root/container/substance",
          substance.getCurrentResource().getPath()) && CollectionUtils.containsAll(
          substance.getLexiconSubstanceReferencePaths(), Arrays.asList("/etc/import-data/solution-product-lookup/00004"))) {
            final List<Resource> resources = new ArrayList<>();
            resources.add(
              substance.getCurrentResource().getResourceResolver().getResource("/etc/import-data/redbook/0920000/0924600/0924649/114451"));
            resources.add(
              substance.getCurrentResource().getResourceResolver().getResource("/etc/import-data/redbook/0920000/0924600/0924649/112912"));
            return Optional.of(new RedBookData(resources, substance.getSubstanceType()));
        } else if (StringUtils.equals(
          "/content/mdx-cem/iv-compatibility/product-monographs/unit-tests/product-2/jcr:content/root/container/substance",
          substance.getCurrentResource().getPath()) && CollectionUtils.containsAll(
          substance.getLexiconSubstanceReferencePaths(), Arrays.asList("/etc/import-data/solution-product-lookup/00004"))) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<RedBookData> getRedBookDataByGcrName(final @NotNull List<String> productNames, final @NotNull ResourceResolver resourceResolver) {
        if (CollectionUtils.containsAll(Arrays.asList("Fat Emulsion", "Fish Oil", "Soybean Oil"), productNames)) {
            final List<Resource> resources = new ArrayList<>();
            resources.add(resourceResolver.getResource("/etc/import-data/redbook/0930000/0933200/0933222"));
            return Optional.of(new RedBookData(resources, SubstanceType.DRUG));
        }
        return Optional.empty();
    }

    @Override
    public Optional<RedBookData> getRedBookDataBySolutionName(final @NotNull Substance substance, final @NotNull ResourceResolver resourceResolver) {
        return Optional.empty();
    }

    @Override
    public Optional<RedBookData> getRedBookDataBySolutionPath(final @NotNull String solutionPath, final @NotNull ResourceResolver resourceResolver) {
        if (StringUtils.equals(solutionPath, "/etc/import-data/solution-product-lookup/01390")) {
            final Resource gfcResource = resourceResolver.getResource("/etc/import-data/redbook/0920000/0924600/0924664/112996");
            return Optional.of(new RedBookData(Arrays.asList(gfcResource), SubstanceType.SOLUTION));
        }
        return Optional.empty();
    }

}
