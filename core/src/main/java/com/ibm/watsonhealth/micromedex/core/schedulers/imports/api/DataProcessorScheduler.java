package com.ibm.watsonhealth.micromedex.core.schedulers.imports.api;

import java.util.Collections;
import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;

import com.ibm.watsonhealth.micromedex.core.schedulers.imports.api.config.DataProcessorSchedulerConfiguration;
import com.ibm.watsonhealth.micromedex.core.services.imports.api.DataProcessor;
import com.ibm.watsonhealth.micromedex.core.utils.ResourceResolverUtils;
import com.ibm.watsonhealth.micromedex.core.utils.RunModeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Runnable.class, immediate = true)
@Designate(ocd = DataProcessorSchedulerConfiguration.class)
public class DataProcessorScheduler implements Runnable {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    private volatile List<DataProcessor> dataProcessors;

    private DataProcessorSchedulerConfiguration config;

    @Activate
    @Modified
    public void activate(final DataProcessorSchedulerConfiguration config) {
        try {
            this.config = config;
        } catch (final Exception ex) {
            log.error("exception while activating/modifying service", ex);
        }
    }

    @Override
    public void run() {
        log.trace("data processor scheduler started.");
        try {
            if (this.config.enabled() && RunModeUtils.isAuthor(this.slingSettingsService)) {
                try (final ResourceResolver serviceResourceResolver = ResourceResolverUtils.getServiceResourceResolver(this.resourceResolverFactory,
                  "importDataProcessorService")) {
                    final List<DataProcessor> currentProcessors = Collections.unmodifiableList(this.dataProcessors);
                    for (final DataProcessor dataProcessor : currentProcessors) {
                        dataProcessor.doProcess(serviceResourceResolver);
                    }
                }
            } else {
                log.info("data processor scheduler is not enabled or runmode is not author --> nothing will be processed.");
            }
        } catch (final Exception ex) {
            log.error("exception while processing imported data", ex);
        }
        log.trace("data processor scheduler finished.");
    }

}
