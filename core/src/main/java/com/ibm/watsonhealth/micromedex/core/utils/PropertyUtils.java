package com.ibm.watsonhealth.micromedex.core.utils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.wcm.api.Page;

/**
 * @author Reinhard Wöss
 *
 */
public final class PropertyUtils {

    private static final String NODE_NULL_EXCEPTION_STRING = "node must not be null";
    private static final String PROPERTY_NULL_EXCEPTION_STRING = "propertyName must not be null";

    private PropertyUtils() {
    }

    /**
     * Updates a property only if 'propertyValue' is different to current value of property and return true in this case.
     * If 'propertyValue' is equal to current value of property nothing will be changed and method returns false.
     * @param node current node
     * @param propertyName name of the property (from type String)
     * @param propertyValue value of the property
     * @return true, if property was changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean updateProperty(final Node node, final String propertyName, final String propertyValue) throws RepositoryException {
        boolean result = false;

        validateParameters(node, propertyName);

        final boolean hasProperty = node.hasProperty(propertyName);
        if (hasProperty && StringUtils.isBlank(propertyValue)) {
            node.getProperty(propertyName).remove();
            result = true;
        } else {
            if (hasProperty) {
                final String currentValue = node.getProperty(propertyName).getString();
                if (!StringUtils.equals(propertyValue, currentValue)) {
                    node.setProperty(propertyName, propertyValue);
                    result = true;
                }
            } else {
                if (StringUtils.isNotBlank(propertyValue)) {
                    node.setProperty(propertyName, propertyValue);
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Updates a property only if 'propertyValue' is different to current value of property and return true in this case.
     * If 'propertyValue' is equal to current value of property nothing will be changed and method returns false.
     * @param node current node
     * @param propertyName name of the property (from type boolean)
     * @param propertyValue value of the property
     * @return true, if property was changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean updateProperty(final Node node, final String propertyName, final boolean propertyValue) throws RepositoryException {
        boolean result = false;

        validateParameters(node, propertyName);

        final boolean hasProperty = node.hasProperty(propertyName);
        if (hasProperty) {
            final boolean currentValue = node.getProperty(propertyName).getBoolean();
            if (propertyValue != currentValue) {
                node.setProperty(propertyName, propertyValue);
                result = true;
            }
        } else {
            node.setProperty(propertyName, propertyValue);
            result = true;
        }

        return result;
    }

    /**
     * Updates a property only if 'propertyValue' is different to current value of property and return true in this case.
     * If 'propertyValue' is equal to current value of property nothing will be changed and method returns false.
     * @param node current node
     * @param propertyName name of the property (from type long)
     * @param propertyValue value of the property
     * @return true, if property was changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean updateProperty(final Node node, final String propertyName, final long propertyValue) throws RepositoryException {
        boolean result = false;

        validateParameters(node, propertyName);

        final boolean hasProperty = node.hasProperty(propertyName);
        if (hasProperty) {
            final long currentValue = node.getProperty(propertyName).getLong();
            if (propertyValue != currentValue) {
                node.setProperty(propertyName, propertyValue);
                result = true;
            }
        } else {
            node.setProperty(propertyName, propertyValue);
            result = true;
        }

        return result;
    }

    /**
     * Updates a property only if 'propertyValue' is different to current value of property and return true in this case.
     * If 'propertyValue' is equal to current value of property nothing will be changed and method returns false.
     * @param node current node
     * @param propertyName name of the property (from type double)
     * @param propertyValue value of the property
     * @return true, if property was changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean updateProperty(final Node node, final String propertyName, final double propertyValue) throws RepositoryException {
        boolean result = false;

        validateParameters(node, propertyName);

        final boolean hasProperty = node.hasProperty(propertyName);
        if (hasProperty) {
            final BigDecimal currentValue = node.getProperty(propertyName).getDecimal();
            final BigDecimal bigDecimalPropertyValue = BigDecimal.valueOf(propertyValue);
            if (bigDecimalPropertyValue.compareTo(currentValue) != 0) {
                node.setProperty(propertyName, bigDecimalPropertyValue);
                result = true;
            }
        } else {
            final BigDecimal bigDecimalPropertyValue = BigDecimal.valueOf(propertyValue);
            node.setProperty(propertyName, bigDecimalPropertyValue);
            result = true;
        }

        return result;
    }

    /**
     * Updates a property only if 'propertyValue' is different to current value of property and return true in this case.
     * If 'propertyValue' is equal to current value of property nothing will be changed and method returns false.
     * @param node current node
     * @param propertyName name of the property (from type ZonedDateTime)
     * @param propertyValue value of the property
     * @return true, if property was changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean updateProperty(final Node node, final String propertyName, final ZonedDateTime propertyValue) throws RepositoryException {
        Calendar propertyValueAsCalendar = null;
        if (propertyValue != null) {
            propertyValueAsCalendar = GregorianCalendar.from(propertyValue);
        }
        return updateProperty(node, propertyName, propertyValueAsCalendar);
    }

    /**
     * Updates a property only if 'propertyValue' is different to current value of property and return true in this case.
     * If 'propertyValue' is equal to current value of property nothing will be changed and method returns false.
     * @param node current node
     * @param propertyName name of the property (from type Calendar)
     * @param propertyValue value of the property
     * @return true, if property was changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean updateProperty(final Node node, final String propertyName, final Calendar propertyValue) throws RepositoryException {
        boolean result = false;

        validateParameters(node, propertyName);

        final boolean hasProperty = node.hasProperty(propertyName);
        if (hasProperty && propertyValue == null) {
            node.getProperty(propertyName).remove();
            result = true;
        } else {
            if (hasProperty) {
                final Property property = node.getProperty(propertyName);
                if (property.isMultiple()) {
                    property.remove();
                    node.setProperty(propertyName, propertyValue);
                    result = true;
                } else {
                    final Calendar currentValue = property.getDate();
                    if (currentValue.getTimeInMillis() != propertyValue.getTimeInMillis()) {
                        node.setProperty(propertyName, propertyValue);
                        result = true;
                    }
                }
            } else {
                if (propertyValue != null) {
                    node.setProperty(propertyName, propertyValue);
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Updates a property only if 'propertyValues' is different to current values of property and return true in this case.
     * If 'propertyValues' is equal to current values of property nothing will be changed and method returns false.
     * @param node current node
     * @param propertyName name of the property (from type String Array)
     * @param propertyValues values of the property
     * @return true, if properties has changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean updateProperty(final Node node, final String propertyName, final List<String> propertyValues) throws RepositoryException {
        boolean result = false;

        validateParameters(node, propertyName);

        final boolean hasProperty = node.hasProperty(propertyName);
        if (hasProperty && propertyValues == null) {
            node.getProperty(propertyName).remove();
            result = true;
        } else {
            if (hasProperty) {
                final Property property = node.getProperty(propertyName);
                if (property.isMultiple()) {
                    final List<String> currentValues = getStringMultiValue(node, propertyName);
                    if (!hasListSameStringValues(currentValues, propertyValues)) {
                        node.setProperty(propertyName, propertyValues.toArray(new String[0]));
                        result = true;
                    }
                } else {
                    property.remove();
                    node.setProperty(propertyName, propertyValues.toArray(new String[0]));
                    result = true;
                }
            } else {
                if (propertyValues != null && !propertyValues.isEmpty()) {
                    node.setProperty(propertyName, propertyValues.toArray(new String[0]));
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Updates a property only if 'propertyValues' is different to current values of property and return true in this case.
     * If 'propertyValues' is equal to current values of property nothing will be changed and method returns false.
     * @param node current node
     * @param propertyName name of the property (from type Long Array)
     * @param propertyValues values of the property
     * @param valueFactory value factory
     * @return true, if properties has changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean updateProperty(final Node node, final String propertyName, final List<Long> propertyValues, final ValueFactory valueFactory) throws RepositoryException {
        boolean result = false;

        validateParameters(node, propertyName);

        final boolean hasProperty = node.hasProperty(propertyName);
        if (hasProperty && propertyValues == null) {
            node.getProperty(propertyName).remove();
            result = true;
        } else {
            if (hasProperty) {
                final Property property = node.getProperty(propertyName);
                if (property.isMultiple()) {
                    final List<Long> currentValues = getLongMultiValue(node, propertyName);
                    if (!hasListSameLongValues(currentValues, propertyValues)) {
                        node.setProperty(propertyName, createValueArray(propertyValues, valueFactory));
                        result = true;
                    }
                } else {
                    property.remove();
                    node.setProperty(propertyName, createValueArray(propertyValues, valueFactory));
                    result = true;
                }
            } else {
                if (propertyValues != null && !propertyValues.isEmpty()) {
                    node.setProperty(propertyName, createValueArray(propertyValues, valueFactory));
                    result = true;
                }
            }
        }

        return result;
    }

    private static boolean hasListSameStringValues(final List<String> list1, final List<String> list2) {
        return list1.size() == list2.size() && list1.containsAll(list2);
    }

    private static boolean hasListSameLongValues(final List<Long> list1, final List<Long> list2) {
        return list1.size() == list2.size() && list1.containsAll(list2);
    }

    /**
     * Updates a property only if the string multifield property 'propertyName' does not contain the value 'propertyValue' .
     * @param node current node
     * @param propertyName name of the property (from type String)
     * @param propertyValue value of the property
     * @return true, if property was changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean extendMultiValueProperty(final Node node, final String propertyName, final String propertyValue) throws RepositoryException {
        boolean result = false;

        validateParameters(node, propertyName);

        if (StringUtils.isNotBlank(propertyValue)) {
            final boolean hasProperty = node.hasProperty(propertyName);
            if (hasProperty) {
                if (node.getProperty(propertyName).isMultiple()) {
                    final List<String> values = getStringMultiValue(node, propertyName);
                    if (!values.contains(propertyValue)) {
                        values.add(propertyValue);
                        node.setProperty(propertyName, values.toArray(new String[0]));
                        result = true;
                    }
                } else {
                    if (!StringUtils.equals(propertyValue, node.getProperty(propertyName).getString())) {
                        final List<String> values = new ArrayList<>();
                        values.add(node.getProperty(propertyName).getString());
                        values.add(propertyValue);
                        node.getProperty(propertyName).remove();
                        node.setProperty(propertyName, values.toArray(new String[0]));
                        result = true;
                    }
                }
            } else {
                final List<String> values = new ArrayList<>();
                values.add(propertyValue);
                node.setProperty(propertyName, values.toArray(new String[0]));
                result = true;
            }
        }

        return result;
    }

    /**
     * Updates a property only if the long multifield property 'propertyName' does not contain the value 'propertyValue' .
     * @param node current node
     * @param propertyName name of the property (from type long)
     * @param propertyValue value of the property
     * @return true, if property was changed, otherwise false.
     * @throws RepositoryException the repository exception
     */
    public static boolean extendMultiValueProperty(final Node node, final String propertyName, final long propertyValue, final ValueFactory valueFactory) throws RepositoryException {
        boolean result = false;

        validateParameters(node, propertyName);

        final boolean hasProperty = node.hasProperty(propertyName);
        if (hasProperty) {
            if (node.getProperty(propertyName).isMultiple()) {
                final List<Long> values = getLongMultiValue(node, propertyName);
                if (!values.contains(propertyValue)) {
                    values.add(propertyValue);
                    node.setProperty(propertyName, createValueArray(values, valueFactory));
                    result = true;
                }
            } else {
                if (propertyValue != node.getProperty(propertyName).getLong()) {
                    final List<Long> values = new ArrayList<>();
                    values.add(node.getProperty(propertyName).getLong());
                    values.add(propertyValue);
                    node.getProperty(propertyName).remove();
                    node.setProperty(propertyName, createValueArray(values, valueFactory));
                    result = true;
                }
            }
        } else {
            final List<Long> values = new ArrayList<>();
            values.add(propertyValue);
            node.setProperty(propertyName, createValueArray(values, valueFactory));
            result = true;
        }

        return result;
    }

    private static Value[] createValueArray(final List<Long> values, final ValueFactory valueFactory) {
        final List<Value> jcrValueList = new ArrayList<>();
        values.forEach(v -> jcrValueList.add(valueFactory.createValue(v)));
        return jcrValueList.toArray(new Value[0]);
    }

    /**
     * Returns all values of a string array property as list
     * @param node current node
     * @param propertyName property name
     * @return list of all string array properties
     * @throws RepositoryException
     */
    public static List<String> getStringMultiValue(final Node node, final String propertyName) throws RepositoryException {
        validateParameters(node, propertyName);

        final List<String> result = new ArrayList<>();
        final Value[] values = node.getProperty(propertyName).getValues();
        for (final Value value : values) {
            result.add(value.getString());
        }
        return result;
    }

    /**
     * Returns all values of a long array property as list
     * @param node current node
     * @param propertyName property name
     * @return list of all long array properties
     * @throws RepositoryException
     */
    public static List<Long> getLongMultiValue(final Node node, final String propertyName) throws RepositoryException {
        validateParameters(node, propertyName);

        final List<Long> result = new ArrayList<>();
        final Value[] values = node.getProperty(propertyName).getValues();
        for (final Value value : values) {
            result.add(value.getLong());
        }
        return result;
    }

    /**
     * Gets the property 'propertyName' from 'page'. If this property is not set it gets the inherated property
     * @param page current page
     * @param propertyName property name
     * @param defaultValue default value if no property value is found at all
     * @param targetClass target class
     * @param <T> type of the property
     * @return
     */
    public static <T> T getPageOrInheritedProperty(final Page page, final String propertyName, final T defaultValue, final Class<T> targetClass) {
        return Optional
          .ofNullable(page.getProperties().get(propertyName, targetClass))
          .orElseGet(() -> getInheritedPageProperty(page, propertyName, defaultValue));
    }

    /**
     * Get the property 'propertyName' from 'page'. If this property is not set it gets the inherated property
     * @param page current page
     * @param propertyName property name
     * @param defaultValue default value if no property value is found at all
     * @param <T> type of the property
     * @return
     */
    public static <T> T getInheritedPageProperty(final Page page, final String propertyName, final T defaultValue) {
        return new HierarchyNodeInheritanceValueMap(page.adaptTo(Resource.class)).getInherited(propertyName, defaultValue);
    }

    private static void validateParameters(final Node node, final String propertyName) {
        if (node == null) {
            throw new IllegalArgumentException(NODE_NULL_EXCEPTION_STRING);
        }
        if (propertyName == null) {
            throw new IllegalArgumentException(PROPERTY_NULL_EXCEPTION_STRING);
        }
    }

}
