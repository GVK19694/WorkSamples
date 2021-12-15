package com.ibm.watsonhealth.micromedex.core.schedulers.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@SuppressWarnings("java:S100")
@ObjectClassDefinition(name = "Pre Replication Queue Scheduler Configuration")
public @interface PreReplicationQueueSchedulerConfiguration {

    @AttributeDefinition(name = "Scheduler Enabled") boolean enabled() default false;

    @AttributeDefinition(name = "Scheduler Expression") String scheduler_expression() default "";

    @AttributeDefinition(name = "Scheduler Concurrent") boolean scheduler_concurrent() default false;

}
