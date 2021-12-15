package com.ibm.watsonhealth.micromedex.core.servlets.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "MDX Health Check Executor Servlet")
public @interface HealthCheckExecutorServletConfiguration {

    @AttributeDefinition(name = "Enabled Health Checks MBeans") String[] enabledHealthCheckNames() default "";

}

