/**
 * Custom Select Box Validations
 */
/* global jQuery, Coral */
(function ($, Coral) {
    "use strict";

    var registry = $(window).adaptTo("foundation-registry");

    registry.register("foundation.validation.validator", {
        selector: "[data-validation=drugLexiconSubstanceReferenceValidation]",
        validate: function (element) {
            var substanceType = getSubstanceType();
            var currentValue = element.value;
            if (substanceType == "Drug" && currentValue == "") {
                return "Error: Please fill out this field";
            }
        }
    });

    registry.register("foundation.validation.validator", {
        selector: "[data-validation=solutionLexiconSubstanceReferenceValidation]",
        validate: function (element) {
            var substanceType = getSubstanceType();
            var currentValue = element.value;
            if (substanceType == "Soln" && currentValue == "") {
                return "Error: Please fill out this field";
            }
        }
    });

    registry.register("foundation.validation.validator", {
        selector: "[data-validation=bothDrugLexiconSubstanceReferenceValidation]",
        validate: function (element) {
            var substanceType = getSubstanceType();
            var substanceReferenceSource = getSubstanceReferenceSource();
            var currentValue = element.value;
            if (substanceType == "Drug-Soln" && substanceReferenceSource == "Drug" && currentValue == "") {
                return "Error: Please fill out this field";
            }
        }
    });

    registry.register("foundation.validation.validator", {
        selector: "[data-validation=bothSolutionLexiconSubstanceReferenceValidation]",
        validate: function (element) {
            var substanceType = getSubstanceType();
            var substanceReferenceSource = getSubstanceReferenceSource();
            var currentValue = element.value;
            if (substanceType == "Drug-Soln" && substanceReferenceSource == "Soln" && currentValue == "") {
                return "Error: Please fill out this field";
            }
        }
    });

    function getSubstanceType() {
        return $("coral-select[name='./substanceType']").find("input[name='./substanceType']")[0].value;
    }

    function getSubstanceReferenceSource() {
        return $("coral-select[name='./lexiconSubstanceReferenceSource']").find("input[name='./lexiconSubstanceReferenceSource']")[0].value;
    }


})(jQuery, Coral);