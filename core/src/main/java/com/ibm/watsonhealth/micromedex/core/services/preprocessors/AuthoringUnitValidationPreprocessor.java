package com.ibm.watsonhealth.micromedex.core.services.preprocessors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.day.cq.replication.Preprocessor;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.services.preprocessors.configurations.AuthoringUnitValidationPreprocessorConfiguration;
import com.ibm.watsonhealth.micromedex.core.utils.ResourceResolverUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Preprocessor.class, immediate = true)
@Designate(ocd = AuthoringUnitValidationPreprocessorConfiguration.class)
public class AuthoringUnitValidationPreprocessor implements Preprocessor {

    private static final String SERVICE_NAME = "authoringUnitValidationPreprocessorService";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private PageManagerFactory pageManagerFactory;

    @Reference
    private SlingSettingsService slingSettingsService;

    private AuthoringUnitValidationPreprocessorConfiguration config;

    @Activate
    @Modified
    public void activate(final AuthoringUnitValidationPreprocessorConfiguration config) {
        this.config = config;
    }

    @Override
    public void preprocess(final @NotNull ReplicationAction replicationAction, final ReplicationOptions replicationOptions) throws ReplicationException {
        if (replicationAction.getType() == ReplicationActionType.ACTIVATE && StringUtils.startsWithAny(
          replicationAction.getPath(), this.config.rootPaths())) {
            try (final ResourceResolver serviceResourceResolver = ResourceResolverUtils.getServiceResourceResolver(this.resourceResolverFactory,
              SERVICE_NAME)) {
                final PageManager pageManager = this.pageManagerFactory.getPageManager(serviceResourceResolver);
                final Page currentPage = pageManager.getContainingPage(replicationAction.getPath());
                if (currentPage != null && this.isResourceTypeToValidate(currentPage) && this.isTemplateToValidate(currentPage)) {
                    final ValidationModel pageModel = currentPage.getContentResource().adaptTo(ValidationModel.class);
                    if (pageModel == null || !pageModel.isValid()) {
                        throw new ReplicationException("Publishing Error. Check the relevant components for errors.");
                    }
                }
            } catch (final RuntimeException | LoginException ex) {
                throw new ReplicationException(ex);
            }
        }
    }

    private boolean isResourceTypeToValidate(@NotNull final Page page) {
        return StringUtils.equalsAny(page.getContentResource().getResourceType(), this.config.resourceTypes());
    }

    private boolean isTemplateToValidate(@NotNull final Page page) {
        return StringUtils.equalsAny(page.getTemplate().getPath(), this.config.templates());
    }

}