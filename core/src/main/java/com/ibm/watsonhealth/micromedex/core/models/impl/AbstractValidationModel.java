package com.ibm.watsonhealth.micromedex.core.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;
import com.ibm.watsonhealth.micromedex.core.models.exceptions.InitException;
import com.ibm.watsonhealth.micromedex.core.services.ValidationService;
import com.ibm.watsonhealth.micromedex.core.vo.validation.ValidationErrorVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractValidationModel implements ValidationModel {

    protected final List<Resource> childResources = new ArrayList<>();

    protected List<ValidationModel> childModels;

    @ScriptVariable
    protected Resource resource;

    @Self
    protected Resource currentResource;

    @OSGiService
    protected ValidationService validationService;

    private boolean valid = true;
    private final List<ValidationErrorVO> errors = new ArrayList<>();
    private final List<ValidationErrorVO> errorsOfSubcomponents = new ArrayList<>();

    @PostConstruct
    protected void postConstruct() {
        try {
            this.searchForChildResources();
            this.applyChildModels();
            this.initBeforeValidation();
            this.validate();
            this.init();
        } catch (final RuntimeException | InitException ex) {
            this.setValid(false);
            this.addError("exception on initializing model");
            log.error("Exception in PostConstruct", ex);
        }
    }

    protected void searchForChildResources() {
        this.childResources.addAll(this.validationService.searchForChildResources(this.getCurrentResource()));
    }

    protected void applyChildModels() {
        this.childModels = this.childResources
          .stream()
          .map(childResource -> childResource.adaptTo(ValidationModel.class))
          .collect(Collectors.toList());
    }

    @Override
    public Resource getCurrentResource() {
        return this.resource == null ? this.currentResource : this.resource;
    }

    public abstract void initBeforeValidation() throws InitException;

    protected void validate() {
        try {
            this.customValidation();
            this.childComponentValidation();
        } catch (final RuntimeException ex) {
            this.setValid(false);
            this.addError("exception on validating component.");
            throw ex;
        }
    }

    protected abstract void customValidation();

    protected void childComponentValidation() {
        for (final ValidationModel childModel : this.childModels) {
            if (childModel == null) {
                this.setValid(false);
                this.addErrorOfSubcomponent(
                  "no validationmodel found for resource in '" + this.getCurrentResource() + "'", this.getCurrentResource().getPath());
            } else {
                this.setValid(this.isValid() && childModel.isValid());
                childModel.getErrors().forEach(error -> this.addErrorOfSubcomponent(error.getMessage(), error.getPath()));
                childModel.getErrorsOfSubcomponents().forEach(error -> this.addErrorOfSubcomponent(error.getMessage(), error.getPath()));
            }
        }
    }

    public abstract void init() throws InitException;

    protected void setValid(final boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return this.valid;
    }

    protected void addError(@NotNull final String message) {
        this.errors.add(new ValidationErrorVO(message, this.getCurrentResource().getPath()));
    }

    protected void addErrorOfSubcomponent(@NotNull final String message, @NotNull final String path) {
        this.errorsOfSubcomponents.add(new ValidationErrorVO(message, path));
    }

    public List<ValidationErrorVO> getErrors() {
        return this.errors;
    }

    public List<ValidationErrorVO> getErrorsOfSubcomponents() {
        return this.errorsOfSubcomponents;
    }

}
