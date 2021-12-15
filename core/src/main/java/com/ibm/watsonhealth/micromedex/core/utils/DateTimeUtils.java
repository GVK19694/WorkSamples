package com.ibm.watsonhealth.micromedex.core.utils;

import java.time.LocalDateTime;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static LocalDateTime convertCalendar2LocalDateTime(@NotNull final Calendar calendar) {
        return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
    }

    /**
     * Returns the value from property 'jcr:created'.<br/>
     * When running unit tests this property will not be available because it's ignored in sling-mocks. In this case the property name 'jcrcreated' will be used.
     * @param valueMap current valuemap
     * @return value of jcr:created property. If this does not exist (e.g. in unit tests) it will return the value of jcrcreated instead. If this also not exists it returns null.
     */
    public static Calendar getCreatedDate(@NotNull final ValueMap valueMap) {
        if (valueMap.containsKey(JcrConstants.JCR_CREATED)) {
            return valueMap.get(JcrConstants.JCR_CREATED, Calendar.class);
        }
        final String propertyNameForUnitTests = StringUtils.remove(JcrConstants.JCR_CREATED, ":");
        if (valueMap.containsKey(propertyNameForUnitTests)) {
            return valueMap.get(propertyNameForUnitTests, Calendar.class);
        }
        return null;
    }

}
