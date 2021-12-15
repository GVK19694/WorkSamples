package com.ibm.watsonhealth.micromedex.core.context;

import java.util.Collections;

import org.apache.sling.testing.mock.sling.ResourceResolverType;

import com.ibm.watsonhealth.micromedex.core.context.utils.ResourceImportUtil;

import io.wcm.testing.mock.aem.junit5.AemContext;

public class AppAemContextBuilder {

    private final AemContext aemContext;

    public AppAemContextBuilder() {
        this.aemContext = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);
    }

    public AppAemContextBuilder(final ResourceResolverType resourceResolverType) {
        this.aemContext = new AemContext(resourceResolverType);
    }

    public AemContext build() {
        return this.aemContext;
    }

    public AppAemContextBuilder loadResources(final String resourcesPath) {
        ResourceImportUtil.loadResources(this.aemContext.load(), resourcesPath);
        return this;
    }

    public <T> AppAemContextBuilder registerInjectActivateService(final T service) {
        this.aemContext.registerInjectActivateService(service, Collections.emptyMap());
        return this;
    }

    public <T> AppAemContextBuilder registerService(final Class<T> serviceClass, final T service) {
        this.aemContext.registerService(serviceClass, service, Collections.emptyMap());
        return this;
    }

    public AppAemContextBuilder loadSlingModels(final String packageName) {
        this.aemContext.addModelsForPackage(packageName);
        return this;
    }

}
