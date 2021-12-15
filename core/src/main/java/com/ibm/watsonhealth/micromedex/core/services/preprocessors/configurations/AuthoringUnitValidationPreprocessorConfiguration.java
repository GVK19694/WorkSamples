package com.ibm.watsonhealth.micromedex.core.services.preprocessors.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Authoring Unit Validation Preprocessor Config")
public @interface AuthoringUnitValidationPreprocessorConfiguration {

    @AttributeDefinition(name = "Root Paths", description = "paths where the validation check has to be done") String[] rootPaths() default "";

    @AttributeDefinition(name = "Resource Types", description = "resource types where the validation check has to be done") String[] resourceTypes() default "";

    @AttributeDefinition(name = "Templates", description = "templates where the validation check has to be done") String[] templates() default "";

}

