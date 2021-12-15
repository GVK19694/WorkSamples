package com.ibm.watsonhealth.micromedex.core.vo.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.watsonhealth.micromedex.core.models.ValidationModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationResultVO {

    @JsonProperty
    private boolean valid;

    @JsonProperty
    private final List<ValidationErrorVO> errors;

    @JsonProperty
    private final List<ValidationErrorVO> errorsOfSubcomponents;

    @JsonProperty
    private List<ErrorsPerSubComponentVO> errorsPerSubComponent;

    public ValidationResultVO(@NotNull final ValidationModel validationModel) {
        this.valid = validationModel.isValid();
        this.errors = validationModel.getErrors();
        this.errorsOfSubcomponents = validationModel.getErrorsOfSubcomponents();
        this.setErrorsPerSubComponent();
    }

    public ValidationResultVO(@NotNull final List<ValidationModel> validationModels) {
        this.valid = true;
        this.errors = new ArrayList<>();
        this.errorsOfSubcomponents = new ArrayList<>();
        for (final ValidationModel validationModel : validationModels) {
            this.valid = this.valid && validationModel.isValid();
            this.errors.addAll(validationModel.getErrors());
            this.errorsOfSubcomponents.addAll(validationModel.getErrorsOfSubcomponents());
        }
        this.setErrorsPerSubComponent();
    }

    private void setErrorsPerSubComponent() {
        this.errorsPerSubComponent = new ArrayList<>();
        for (final ValidationErrorVO errorOfSubcomponent : this.errorsOfSubcomponents) {
            final ErrorsPerSubComponentVO currentErrors = this.errorsPerSubComponent
              .stream()
              .filter(item -> StringUtils.equals(item.getPath(), errorOfSubcomponent.getPath()))
              .findFirst()
              .orElse(new ErrorsPerSubComponentVO(errorOfSubcomponent.getPath()));
            if (!this.errorsPerSubComponent.contains(currentErrors)) {
                this.errorsPerSubComponent.add(currentErrors);
            }
            currentErrors.addError(errorOfSubcomponent);
        }
    }

}
