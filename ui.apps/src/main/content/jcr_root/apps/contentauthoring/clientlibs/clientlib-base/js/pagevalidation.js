var validationAlreadyRunning = false;

function revalidate() {
    if (!validationAlreadyRunning) {
        validationAlreadyRunning = true;
        let divPageValdiation = $("div.pagevalidation.validationinfo");
        let url = divPageValdiation.attr("data-validation-url");

        $(".marker").each(function (index, marker) {
            $(marker).addClass("loading-marker");
            $(marker).removeClass("valid-marker");
            $(marker).removeClass("invalid-marker");
            let errorsUl = $(marker).closest("div.validationinfo").find(".errors");
            errorsUl.find("li").detach();
        });

        $.ajax({
            url: url, success: function (result) {
                updateValidationData(result);
                validationAlreadyRunning = false;
            }, error: function (result) {
                validationAlreadyRunning = false;
            }
        });
    }
}

function updateValidationData(validationResult) {
    let pageValidationMarker = $("div.pagevalidation.validationinfo").find(".marker");
    if (validationResult.valid) {
        pageValidationMarker.removeClass("loading-marker");
        pageValidationMarker.addClass("valid-marker");
        pageValidationMarker.removeClass("invalid-marker");
    } else {
        pageValidationMarker.removeClass("loading-marker");
        pageValidationMarker.removeClass("valid-marker");
        pageValidationMarker.addClass("invalid-marker");
    }

    let errorsUl = $(pageValidationMarker).closest("div.validationinfo").find(".errors");
    validationResult.errors.forEach((errorItem) => {
        errorsUl.append("<li>" + errorItem.message + "</li>");
    });

    validationResult.errorsPerSubComponent.forEach((item) => {
        let currentMarker = $("div.validationinfo[data-component-path='" + item.path + "']").find(".marker");
        currentMarker.removeClass("loading-marker");
        currentMarker.removeClass("valid-marker");
        currentMarker.addClass("invalid-marker");

        let errorsUl = $(currentMarker).closest("div.validationinfo").find(".errors");
        item.errors.forEach((errorItem) => {
            errorsUl.append("<li>" + errorItem.message + "</li>");
        });

        let allValidationDivs = $("div.validationinfo");
        allValidationDivs.each(function (index, validationDiv) {
            let validationDivPath = $(validationDiv).attr("data-component-path");
            if (isItemToInvalidate(item.path, validationDivPath)) {
                let validationDivMarker = $(validationDiv).find(".marker");
                validationDivMarker.removeClass("loading-marker");
                validationDivMarker.removeClass("valid-marker");
                validationDivMarker.addClass("invalid-marker");
            }
        });
    });

    let markers = $(".marker");
    markers.each(function (index, marker) {
        if ($(marker).hasClass("loading-marker")) {
            $(marker).removeClass("loading-marker");
            $(marker).addClass("valid-marker");
            $(marker).removeClass("invalid-marker");
        }
    });
}


function isItemToInvalidate(pathOfInvalidItem, currentPath) {
    let invalidate = pathOfInvalidItem.startsWith(currentPath);
    if (invalidate) {
        let relativePath = pathOfInvalidItem.replace(currentPath, "");
        invalidate = relativePath == "" || relativePath.startsWith("/");
    }
    return invalidate;
}