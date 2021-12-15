# Page Pathfield Picker

This picker can be used for the pathfield dialog field (`granite/ui/components/coral/foundation/form/pathfield`).<br/>
When using this picker the search result in the pathfield return pages instead of nodes only.

This node is a copy of `/libs/granite/ui/content/coral/foundation/form/pathfield/picker` with some small adaptions.

## Adaptions

`/apps/contentauthoring/dialogfields/pagepathfieldpicker/search/form/rootpath`: The resourcetype was changed
to `granite/ui/components/coral/foundation/form/hidden` to hide the root path field in the filter section.

`/apps/contentauthoring/dialogfields/pagepathfieldpicker/search/form/type` was added to adapt the search to return results of type cq:Page instead of nt:
unstructured only.

## Usage

When you want to use the picker in your pathfield then you have only have to add a new property to your field:

`pickerSrc="/mnt/overlay/contentauthoring/dialogfields/pagepathfieldpicker.html?_charset_=utf-8&amp;path={value}&amp;root=<add-your-custom-root-path-here>&amp;filter=hierarchyNotFile&amp;selectionCount=single"`

You only have to adapt the root parameter by replacing '<add-your-custom-root-path-here>' by the same value you are using for the 'rootPath' property.

### Example

```
<citation
    jcr:primaryType="nt:unstructured"
    sling:resourceType="granite/ui/components/coral/foundation/form/pathfield"
    required="{Boolean}true"
    fieldLabel="Citation"
    rootPath="/content/mdx-cem/citation"
    pickerSrc="/mnt/overlay/contentauthoring/dialogfields/pagepathfieldpicker.html?_charset_=utf-8&amp;path={value}&amp;root=/content/mdx-cem/citation&amp;filter=hierarchyNotFile&amp;selectionCount=single"
    name="./citationPath"/>
```

In this example you can see the same path in rootPath and in the root parameter in pickerSrc.
