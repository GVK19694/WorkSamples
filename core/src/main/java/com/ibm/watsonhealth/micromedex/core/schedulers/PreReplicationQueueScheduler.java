package com.ibm.watsonhealth.micromedex.core.schedulers;

import java.util.List;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.ibm.watsonhealth.micromedex.core.schedulers.config.PreReplicationQueueSchedulerConfiguration;
import com.ibm.watsonhealth.micromedex.core.services.PreReplicationQueueService;
import com.ibm.watsonhealth.micromedex.core.services.impl.PreReplicationQueueServiceImpl;
import com.ibm.watsonhealth.micromedex.core.utils.ResourceResolverUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = Runnable.class, immediate = true)
@Designate(ocd = PreReplicationQueueSchedulerConfiguration.class)
public class PreReplicationQueueScheduler implements Runnable {

    @SuppressWarnings("java:S1450")
    private PreReplicationQueueSchedulerConfiguration config;

    @Reference
    private PreReplicationQueueService preReplicationQueueService;

    @Reference
    private Replicator replicator;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    @Modified
    public void activate(final PreReplicationQueueSchedulerConfiguration config) {
        try {
            this.config = config;
        } catch (final Exception ex) {
            log.error("exception while activating/modifying service", ex);
        }
    }

    @Override
    public void run() {
        log.trace("pre replication queue scheduler started.");
        try {
            try (final ResourceResolver serviceResourceResolver = ResourceResolverUtils.getServiceResourceResolver(this.resourceResolverFactory,
              PreReplicationQueueServiceImpl.SERVICE_NAME)) {
                final Session session = serviceResourceResolver.adaptTo(Session.class);
                if (session != null) {
                    final List<String> queueIds = this.preReplicationQueueService.getQueueIds();
                    log.info("found {} queues", queueIds.size());
                    for (final String queueId : queueIds) {
                        final List<String> pathsToReplicate = this.preReplicationQueueService.getPathsToReplicate(queueId);
                        log.info("start replication of {} nodes from queue '{}'", pathsToReplicate.size(), queueId);
                        for (final String pathToReplicate : pathsToReplicate) {
                            if (StringUtils.startsWith(pathToReplicate, PreReplicationQueueServiceImpl.DEACTIVATE_PREFIX)) {
                                final String pathToDeactivate = StringUtils.removeStart(
                                  pathToReplicate, PreReplicationQueueServiceImpl.DEACTIVATE_PREFIX);
                                this.replicator.replicate(session, ReplicationActionType.DEACTIVATE, pathToDeactivate);
                            } else {
                                this.replicator.replicate(session, ReplicationActionType.ACTIVATE, pathToReplicate);
                            }
                        }
                        log.info("finished replication of {} nodes from queue '{}'", pathsToReplicate.size(), queueId);
                        this.preReplicationQueueService.deleteQueue(queueId);
                    }
                }
            }
        } catch (final Exception ex) {
            log.error("exception while processing imported data", ex);
        }
        log.trace("pre replication queue scheduler finished.");
    }

}
