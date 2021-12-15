<%--
# External UI Integration

This field can be used to access an externally hosted webpage and retrieve data from that page when it is sent back via `window.parent.postMessage()`

This node is a copy of `/libs/granite/ui/components/coral/foundation/form/textfield` with some small adaptions.

## Adaptions

A button has been added that opens the given url in an overlay which includes an IFrame.

A cq:dialog property `externalUrl` has been added which needs to contain a valid url.

## Usage

When you want to use the field in your component then you have to set the `resourceType` to `contentauthoring/dialogfields/externalUiIntegration`

You also need to set the value for `externalUrl`

### Example

<external-ui-integration
    jcr:primaryType="nt:unstructured"
    sling:resourceType="contentauthoring/dialogfields/externalUiIntegration"
    name="./externalUi"
    externalUrl="http://localhost:8080/ExternalUI"
    emptyText="Selected data goes here"
    fieldLabel="External UI"/>

In this example you can see the usage of the custom property `externalUrl` in addition to some of the usual `Textfield` properties.
--%>

<%@ include file="/libs/granite/ui/global.jsp" %>
<%@ page session="false" import="org.apache.commons.lang3.StringUtils,
                                 org.apache.sling.api.resource.ValueMap,
                                 com.adobe.granite.ui.components.AttrBuilder,
                                 com.adobe.granite.ui.components.Config,
                                 com.adobe.granite.ui.components.Field,
                                 com.adobe.granite.ui.components.Tag" %>
<% final Config cfg = cmp.getConfig();
    final ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());
    final Field field = new Field(cfg);

    final String name = cfg.get("name", String.class);

    final boolean isMixed = field.isMixed(cmp.getValue());

    final Tag tag = cmp.consumeTag();
    final AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    attrs.add("type", "text");
    attrs.add("name", name);
    attrs.add("placeholder", i18n.getVar(cfg.get("emptyText", String.class)));
    attrs.add("aria-label", i18n.getVar(cfg.get("emptyText", String.class)));
    attrs.addDisabled(cfg.get("disabled", false));
    attrs.add("autocomplete", cfg.get("autocomplete", String.class));
    attrs.addBoolean("autofocus", cfg.get("autofocus", false));

    final String fieldLabel = cfg.get("fieldLabel", String.class);
    final String fieldDesc = cfg.get("fieldDescription", String.class);
    String labelledBy = null;

    if (fieldLabel != null && fieldDesc != null) {
        labelledBy = vm.get("labelId", String.class) + " " + vm.get("descriptionId", String.class);
    } else if (fieldLabel != null) {
        labelledBy = vm.get("labelId", String.class);
    } else if (fieldDesc != null) {
        labelledBy = vm.get("descriptionId", String.class);
    }

    if (StringUtils.isNotBlank(labelledBy)) {
        attrs.add("labelledby", labelledBy);
    }

    if (isMixed) {
        attrs.addClass("foundation-field-mixed");
        attrs.add("placeholder", i18n.get("<Mixed Entries>"));
        attrs.add("aria-label", i18n.get("<Mixed Entries>"));
    } else {
        attrs.add("value", vm.get("value", String.class));
    }

    attrs.add("maxlength", cfg.get("maxlength", Integer.class));

    if (cfg.get("required", false)) {
        attrs.add("aria-required", true);
    }

    final String validation = StringUtils.join(cfg.get("validation", new String[0]), " ");
    attrs.add("data-foundation-validation", validation);
    attrs.add("data-validation", validation); // Compatibility

    //Custom adaptations
    attrs.addClass("coral-Form-field");
    attrs.addClass("coral3-Textfield");
    attrs.add("externalUrl", cfg.get("externalUrl", String.class));
    attrs.add("is", "coral-textfield");%>

<div class="coral-InputGroup coral-InputGroup--block">
    <input <%= attrs.build() %> hidden/>
    <label class="coral-Form-field coral3-Textfield" name="<%=name%>" onclick="openExternalUi('<%=name%>')"></label>
    <span class="coral-InputGroup-button">
        <button is="coral-button" type="button" onclick="openExternalUi('<%=name%>')" style="margin-top: -0.39rem;">Open</button>
    </span>
</div>

<script>
    let externalUiDialog;
    let externalUiUrl = "";

    function openExternalUi(nameAttribute) {
        if (externalUiDialog) {
            $(externalUiDialog).detach();
        }

        let taxonomy = "000DEB5F-00F0-F1CD-386E-80A6AC1D3432";
        let selectedFormulationType = $('input[name="./formulationType"]')[0].value;
        if (selectedFormulationType === "Grouping-Name") {
            taxonomy = "0017D026-00ED-490E-0664-8013AC1D2173";
        }
        externalUiUrl = "<%= attrs.getData().get("externalUrl") %>" + "?taxonomy=" + taxonomy;

        externalUiDialog = new Coral.Dialog().set({
            id: "externalUiDialog",
            header: {
                innerHTML: 'Lexicon browser'
            },
            content: {
                innerHTML: '<iframe style="width: 80vw; height: 80vh; border: 0" src="' + externalUiUrl + '"/>'
            },
            closable: "on"
        });

        document.body.append(externalUiDialog);
        externalUiDialog.setAttribute("inputId", $("input[name='" + nameAttribute + "']")[0].getAttribute('id'));
        externalUiDialog.show();
    }


    window.onmessage = function (e) {
        let pattern = ".com";
        let origin = externalUiUrl.substr(0, externalUiUrl.indexOf(pattern) + pattern.length);
        if (e.origin === origin) {
            const inputId = externalUiDialog.getAttribute("inputId");
            const inputField = $("#" + inputId);
            inputField[0].setAttribute("value", e.data.guid + "@/@" + e.data.conceptTermName);
            updateLabelField(inputField);

            externalUiDialog.hide();
            $(externalUiDialog).detach();
        }
    }

    function updateLabelField(inputField) {
        let fieldValue = inputField[0].getAttribute("value");
        let labelField = inputField.parent().find("label");
        let labelText = "";
        if (fieldValue != null && fieldValue !== "") {
            let items = fieldValue.split("@/@");
            labelText = items[1] + " [" + items[0] + "]";
        }
        labelField.text(labelText);
    }

    updateLabelField($("input[name='<%=name%>']"));
</script>