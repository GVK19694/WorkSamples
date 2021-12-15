package com.ibm.watsonhealth.micromedex.core.schedulers.imports.api.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@SuppressWarnings("java:S100")
@ObjectClassDefinition(name = "Import API Data Processor Scheduler Configuration")
public @interface DataProcessorSchedulerConfiguration {

    @AttributeDefinition(name = "Scheduler Enabled") boolean enabled() default false;

    @AttributeDefinition(name = "Scheduler Expression") String scheduler_expression() default "";

    @AttributeDefinition(name = "Scheduler Concurrent") boolean scheduler_concurrent() default false;

}
