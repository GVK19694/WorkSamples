package com.ibm.watsonhealth.micromedex.core.services;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.ibm.watsonhealth.micromedex.core.services.exceptions.PreReplicationQueueException;

public interface PreReplicationQueueService {

    @NotNull String createQueue() throws PreReplicationQueueException;

    void addToQueue(final String path, final String queueId) throws PreReplicationQueueException;

    void addToQueueDeactivate(final String path, final String queueId) throws PreReplicationQueueException;

    void commit(final String queueId) throws PreReplicationQueueException;

    void cancel(final String queueId) throws PreReplicationQueueException;

    void releaseQueue(final String queueId) throws PreReplicationQueueException;

    List<String> getQueueIds() throws PreReplicationQueueException;

    List<String> getPathsToReplicate(final String queueId) throws PreReplicationQueueException;

    void deleteQueue(final String queueId) throws PreReplicationQueueException;

}
