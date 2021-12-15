// New coral text field to be use as filter input
function newFilterTextField() {
    var filter = new Coral.Textfield();
    filter.classList.add("coral-Form-field");
    filter.placeholder = "Search by solution name, other names or gfc codes";
    return filter;
}

// Filter the select item based on the filter text
function filterItems(filterText) {
    var selectItemList = [];
    jsonObj.forEach(jsonData => {
        var containsFilterText = containsInName(jsonData.name, filterText) || containsInOtherNames(jsonData.otherNames, filterText) || containsInGfcsCode(jsonData.gfcs, filterText);
        if (containsFilterText) {
            selectItemList.push(jsonData.name.toLowerCase());
        }
    });
    return selectItemList;
}

function containsInName(name, filterText) {
    return name.trim().toLowerCase().indexOf(filterText.toLowerCase()) >= 0;
}

function containsInOtherNames(otherNames, filterText) {
    var result = false;
    if (otherNames != undefined) {
        otherNames.forEach(item => {
            if (item.trim().toLowerCase().indexOf(filterText.toLowerCase()) >= 0) {
                result = true;
            }
        });
    }
    return result;
}

function containsInGfcsCode(gfcCodes, filterText) {
    var result = false;
    if (gfcCodes != undefined) {
        gfcCodes.forEach(item => {
            if (item.toString().indexOf(filterText) >= 0) {
                result = true;
            }
        })
    }
    return result;
}


// Show the item which matches the filter text
function enableFilteredItem(filterText, uniqueItemList, item) {
    var itemText = item.textContent.toLowerCase();
    var bothNotEmpty = itemText && filterText;
    var hideItem = true;
    var eitherIsEmpty = !itemText || !filterText;
    var uniqueItem;
    for (var j = 0; j < uniqueItemList.length; j++) {
        uniqueItem = uniqueItemList[j].toLowerCase();
        if (eitherIsEmpty || (bothNotEmpty && itemText.indexOf(uniqueItem) > -1 && itemText.length === uniqueItem.length)) {
            hideItem = false;
            item.show();
        }
    }

    if (hideItem) {
        item.hide();
    }
}

// Remove the duplicate item in the list
function removeDuplicate(selectItemList) {
    // Array to keep track of duplicates
    var uniqueSelectItemList = [];
    var itemList = selectItemList.filter(function (el) {
        // If it is not a duplicate, return true
        if (uniqueSelectItemList.indexOf(el) == -1) {
            uniqueSelectItemList.push(el);
            return true;
        }
        return false;
    });
    return uniqueSelectItemList;
}

/**
 * Hides all items on the selectListEl that do not contain the filterText, ignoring case.
 * @param {*} selectListEl the "coral-selectlist" element.
 * @param {*} filterText the search string (filter).
 */
function filterLexiconSelectList(selectListEl, filterText) {
    var selectItemList = filterItems(filterText.trim());
    var uniqueItemList = removeDuplicate(selectItemList);
    selectListEl.items.getAll().forEach(item => {
        enableFilteredItem(filterText, uniqueItemList, item);
    });
}

var jsonObj;
$(document).on("foundation-contentloaded", function (e) {
    var container = e.target;
    $('coral-select[class~="solution-selectbox-custom-filter"]', container).each((i, el) => {
        Coral.commons.ready(el, function (selectEl) {
            // Fetch the json data from the given path
            $.ajax({
                url: "/secureservlets/solutionproductlookup", success: function (result) {
                    jsonObj = result;
                }
            });
            // Get the taglist field. this is only available if multiselect is enabled
            var taglist = $(selectEl).children('coral-taglist');

            var filter = newFilterTextField();
            var overlay = selectEl.querySelector("coral-overlay");
            var selectList = overlay.querySelector("coral-selectlist");
            // add the filter field to the beginning of the list
            selectList.items.add(filter, selectList.items.first());
            // apply filter on keyup
            filter.addEventListener("keyup", function () {
                // If the taglist field exist (multiselect is enbaled) then it will fetch the html of select list and assign to the taglist field.
                if (taglist.length > 0) {
                    existingData = taglist[0].innerHTML;
                    taglist[0].innerHTML = existingData;
                }
                var filterValue = filter.value;
                filterLexiconSelectList(selectList, filterValue);
            });
        });
    });
});
