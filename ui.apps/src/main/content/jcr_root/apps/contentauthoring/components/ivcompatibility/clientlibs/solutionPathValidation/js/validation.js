/**
 * Allows to enable / disable the validation of a selectbox based on the selection of an checkbox. If the the checkbox is selected then the validation is turned on,
 * if the checkbox is not selected then no validation will be done.
 *
 * How to use:
 *
 * - add the class 'solutionpath-enable-validation-checkbox' to the checkbox element which enables/disables the validation
 */
/* global jQuery, Coral */
(function ($, Coral) {
    "use strict";

    var registry = $(window).adaptTo("foundation-registry");

    registry.register("foundation.validation.validator", {
        selector: "[data-validation=solutionPathValidation]",
        validate: function (element) {
            var $target = $(".solutionpath-enable-validation-checkbox");
            var checked = $target[0].checked;

            if (checked) {
                if (isSolutionPathEmpty(element)) {
                    return "Error: Please fill out this field";
                }
            }
        }
    });

    function isSolutionPathEmpty(element) {
        let $element = $(element);
        return $element.find("input[handle='input']")[0].value == "";
    }

})(jQuery, Coral);