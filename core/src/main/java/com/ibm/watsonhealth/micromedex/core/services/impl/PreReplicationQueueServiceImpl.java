package com.ibm.watsonhealth.micromedex.core.services.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.watsonhealth.micromedex.core.services.PreReplicationQueueService;
import com.ibm.watsonhealth.micromedex.core.services.exceptions.PreReplicationQueueException;
import com.ibm.watsonhealth.micromedex.core.utils.PropertyUtils;
import com.ibm.watsonhealth.micromedex.core.utils.ResourceResolverUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(service = PreReplicationQueueService.class)
public class PreReplicationQueueServiceImpl implements PreReplicationQueueService {

    public static final String DEACTIVATE_PREFIX = "-";

    public static final String SERVICE_NAME = "preReplicationService";

    @SuppressWarnings("java:S1075")
    private static final String PRE_REPLICATION_QUEUES_ROOT_PATH = "/etc/preReplication/queues";

    private static final String PROPERTY_NAME_PATHS = "paths";
    private static final int MAX_PATHS_PER_PROPERTY = 1000;
    private static final int MAX_NUM_PATH_PROPERTYS = 1000;
    private static final String PROPERTY_NAME_RELEASED = "released";
    private static final int MAX_QUEUE_CREATION_ATTEMPTS = 100;
    private static final NumberFormat FIVE_DIGIT_FORMATTER = new DecimalFormat("#00000");

    private static final String DEFAULT_EXCEPTION_MESSAGE = "exception happened";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private final HashMap<String, ResourceResolver> serviceResourceResolvers = new HashMap<>();

    @Override
    @NotNull
    public String createQueue() throws PreReplicationQueueException {
        try {
            final ResourceResolver serviceResourceResolver = ResourceResolverUtils.getServiceResourceResolver(
              this.resourceResolverFactory, SERVICE_NAME);
            boolean queueExists = true;
            int counter = 0;
            UUID queueId = null;
            while (queueExists && counter < MAX_QUEUE_CREATION_ATTEMPTS) {
                queueId = UUID.randomUUID();
                queueExists = this.queueExists(queueId.toString(), serviceResourceResolver);
                counter++;
            }
            if (counter >= MAX_QUEUE_CREATION_ATTEMPTS) {
                throw new PreReplicationQueueException("not able to create queue");
            }
            this.createQueueNode(queueId.toString(), serviceResourceResolver);
            this.serviceResourceResolvers.put(queueId.toString(), serviceResourceResolver);
            return queueId.toString();
        } catch (final LoginException | PersistenceException ex) {
            throw new PreReplicationQueueException(DEFAULT_EXCEPTION_MESSAGE, ex);
        }
    }

    @Override
    public void addToQueue(final String path, final String queueId) throws PreReplicationQueueException {
        try {
            final ResourceResolver serviceResourceResolver = this.serviceResourceResolvers.get(queueId);
            final Optional<Resource> queueResource = this.getPreReplicationQueueResource(queueId, serviceResourceResolver);
            if (queueResource.isPresent()) {
                if (this.isReleased(queueResource.get())) {
                    throw new PreReplicationQueueException("queue '" + queueId + "' is already released");
                }
                final Node queueNode = queueResource.get().adaptTo(Node.class);
                if (queueNode != null) {
                    PropertyUtils.extendMultiValueProperty(queueNode, this.getPathsProperty(queueResource.get()), path);
                }
            } else {
                throw new PreReplicationQueueException("queueResource not found (queueId = '" + queueId + "')");
            }
        } catch (final RepositoryException ex) {
            throw new PreReplicationQueueException(DEFAULT_EXCEPTION_MESSAGE, ex);
        }
    }

    @Override
    public void addToQueueDeactivate(final String path, final String queueId) throws PreReplicationQueueException {
        this.addToQueue(DEACTIVATE_PREFIX + path, queueId);
    }

    @Override
    public void commit(final String queueId) throws PreReplicationQueueException {
        try {
            this.serviceResourceResolvers.get(queueId).commit();
        } catch (final PersistenceException ex) {
            throw new PreReplicationQueueException("not able to commit", ex);
        }
    }

    @Override
    public void cancel(final String queueId) throws PreReplicationQueueException {
        this.serviceResourceResolvers.get(queueId).revert();
        this.serviceResourceResolvers.get(queueId).close();
        this.serviceResourceResolvers.remove(queueId);
        this.deleteQueue(queueId);
    }

    @Override
    public void releaseQueue(final String queueId) throws PreReplicationQueueException {
        try {
            final ResourceResolver serviceResourceResolver = this.serviceResourceResolvers.get(queueId);
            final Optional<Resource> queueResource = this.getPreReplicationQueueResource(queueId, serviceResourceResolver);
            if (queueResource.isPresent()) {
                if (this.isReleased(queueResource.get())) {
                    throw new PreReplicationQueueException("queue '" + queueId + "' is already released");
                }
                final Node queueNode = queueResource.get().adaptTo(Node.class);
                if (queueNode != null) {
                    PropertyUtils.updateProperty(queueNode, PROPERTY_NAME_RELEASED, true);
                    serviceResourceResolver.commit();
                }
                serviceResourceResolver.close();
                this.serviceResourceResolvers.remove(queueId);
            } else {
                throw new PreReplicationQueueException("queueResource not found (queueId = '" + queueId + "')");
            }
        } catch (final RepositoryException | PersistenceException ex) {
            throw new PreReplicationQueueException(DEFAULT_EXCEPTION_MESSAGE, ex);
        }
    }

    @Override
    @NotNull
    public List<String> getQueueIds() throws PreReplicationQueueException {
        final List<String> result = new ArrayList<>();
        try (final ResourceResolver serviceResourceResolver = ResourceResolverUtils.getServiceResourceResolver(this.resourceResolverFactory,
          SERVICE_NAME)) {
            final Optional<Resource> rootResource = this.getPreReplicationRootResource(serviceResourceResolver);
            if (rootResource.isPresent()) {
                final Iterator<Resource> queueResourcesIterator = rootResource.get().listChildren();
                Resource queueResource;
                while (queueResourcesIterator.hasNext()) {
                    queueResource = queueResourcesIterator.next();
                    if (this.isReleased(queueResource)) {
                        result.add(queueResource.getName());
                    }
                }
            }
        } catch (final LoginException ex) {
            throw new PreReplicationQueueException(DEFAULT_EXCEPTION_MESSAGE, ex);
        }
        return result;
    }

    @Override
    @NotNull
    public List<String> getPathsToReplicate(@NotNull final String queueId) throws PreReplicationQueueException {
        try (final ResourceResolver serviceResourceResolver = ResourceResolverUtils.getServiceResourceResolver(this.resourceResolverFactory,
          SERVICE_NAME)) {
            final Optional<Resource> queueResource = this.getPreReplicationQueueResource(queueId, serviceResourceResolver);
            if (queueResource.isPresent()) {
                final ValueMap valueMap = queueResource.get().getValueMap();
                final List<String> pathProperties = valueMap
                  .keySet()
                  .stream()
                  .filter(key -> StringUtils.startsWith(key, PROPERTY_NAME_PATHS))
                  .sorted()
                  .collect(Collectors.toList());
                final List<String> result = new ArrayList<>();
                String[] values;
                for (final String pathProperty : pathProperties) {
                    values = valueMap.get(pathProperty, String[].class);
                    if (values != null) {
                        CollectionUtils.addAll(result, values);
                    }
                }
                return result;
            }
            return new ArrayList<>();
        } catch (final LoginException ex) {
            throw new PreReplicationQueueException(DEFAULT_EXCEPTION_MESSAGE, ex);
        }
    }

    @Override
    public void deleteQueue(@NotNull final String queueId) throws PreReplicationQueueException {
        try (final ResourceResolver serviceResourceResolver = ResourceResolverUtils.getServiceResourceResolver(this.resourceResolverFactory,
          SERVICE_NAME)) {
            final Optional<Resource> queueResource = this.getPreReplicationQueueResource(queueId, serviceResourceResolver);
            if (queueResource.isPresent()) {
                serviceResourceResolver.delete(queueResource.get());
                serviceResourceResolver.commit();
            }
        } catch (final LoginException | PersistenceException ex) {
            throw new PreReplicationQueueException(DEFAULT_EXCEPTION_MESSAGE, ex);
        }
    }

    private Optional<Resource> getPreReplicationRootResource(@NotNull final ResourceResolver resourceResolver) {
        return Optional.ofNullable(resourceResolver.getResource(PRE_REPLICATION_QUEUES_ROOT_PATH));
    }

    private Optional<Resource> getPreReplicationQueueResource(@NotNull final String queueId, @NotNull final ResourceResolver resourceResolver) {
        return this.getPreReplicationRootResource(resourceResolver).map(resource -> resource.getChild(queueId));
    }

    private boolean queueExists(@NotNull final String queueId, @NotNull final ResourceResolver resourceResolver) {
        final Optional<Resource> rootResource = this.getPreReplicationRootResource(resourceResolver);
        return rootResource.map(resource -> resource.getChild(queueId) != null).orElse(false);
    }

    private void createQueueNode(@NotNull final String queueId, @NotNull final ResourceResolver resourceResolver) throws PersistenceException, PreReplicationQueueException {
        final Optional<Resource> rootResource = this.getPreReplicationRootResource(resourceResolver);
        if (rootResource.isPresent()) {
            resourceResolver.create(rootResource.get(), queueId, null);
            return;
        }
        throw new PreReplicationQueueException("was not able to create queue node with id '" + queueId + "'");
    }

    private boolean isReleased(@NotNull final Resource resource) {
        final ValueMap valueMap = resource.getValueMap();
        return valueMap.containsKey(PROPERTY_NAME_RELEASED) && valueMap.get(PROPERTY_NAME_RELEASED, false);
    }

    private String getPathsProperty(@NotNull final Resource queueResource) throws PreReplicationQueueException {
        final ValueMap valueMap = queueResource.getValueMap();
        final List<String> pathsPropertyNames = valueMap
          .keySet()
          .stream()
          .filter(key -> StringUtils.startsWith(key, PROPERTY_NAME_PATHS))
          .sorted()
          .collect(Collectors.toList());
        if (!pathsPropertyNames.isEmpty()) {
            final String lastPathsProperty = pathsPropertyNames.get(pathsPropertyNames.size() - 1);
            final String[] values = valueMap.get(lastPathsProperty, String[].class);
            if (values != null && values.length < MAX_PATHS_PER_PROPERTY) {
                return lastPathsProperty;
            } else {
                final int nextIndex = Integer.parseInt(lastPathsProperty.split("_")[1]) + 1;
                if (nextIndex > MAX_NUM_PATH_PROPERTYS) {
                    throw new PreReplicationQueueException("not more than " + MAX_NUM_PATH_PROPERTYS + " paths properties are allowed in one queue");
                }
                return this.getPathsPropertyByIndex(nextIndex);
            }
        }

        return this.getPathsPropertyByIndex(1);
    }

    private String getPathsPropertyByIndex(final int index) {
        return PROPERTY_NAME_PATHS + "_" + FIVE_DIGIT_FORMATTER.format(index);
    }

}
