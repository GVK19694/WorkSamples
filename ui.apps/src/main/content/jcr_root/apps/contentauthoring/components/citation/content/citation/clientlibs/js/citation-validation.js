/* global jQuery, Coral */
(function ($, Coral) {
    "use strict";

    var registry = $(window).adaptTo("foundation-registry");

    registry.register("foundation.validation.validator", {
        selector: "[data-validation=accessedOnValidation]",
        validate: function (element) {
            if (getCitationType() == "electronic") {
                let el = $(element);
                return validate(el, element.getAttribute("data-validationpattern"), element.getAttribute("data-validationmessage"));
            }
        }
    });

    function getCitationType() {
        let element = document.getElementsByName("./citationType")[0];
        let el = $(element);
        return el.val();
    }

    function validate(element, pattern, message) {
        let value = element.val();
        let regEx = new RegExp(pattern, "g");
        let regExTest = regEx.test(value);
        if (value && !regExTest) {
            return message;
        }
    }

})(jQuery, Coral);