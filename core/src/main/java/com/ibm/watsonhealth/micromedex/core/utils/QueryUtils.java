package com.ibm.watsonhealth.micromedex.core.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.ResourceResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Reinhard Wöss
 *
 */
@Slf4j
public final class QueryUtils {

    private QueryUtils() {
    }

    public static NodeIterator getQueryNodes(final Session session, final String queryString) throws RepositoryException {
        final Query query = session.getWorkspace().getQueryManager().createQuery(queryString, Query.JCR_SQL2);
        final NodeIterator resultNodes = query.execute().getNodes();

        if (log.isTraceEnabled()) {
            log.trace("query: {}, size: {}", queryString, resultNodes.getSize());
        }
        return resultNodes;
    }

    public static List<String> getQueryResultPaths(final Session session, final String queryString) throws RepositoryException {
        final List<String> result = new ArrayList<>();
        final NodeIterator queryResultIterator = getQueryNodes(session, queryString);
        while (queryResultIterator.hasNext()) {
            result.add(queryResultIterator.nextNode().getPath());
        }
        return result;
    }

    public static NodeIterator searchByProperty(final String queryRootPath, final String propertyName, final String propertyValue, final ResourceResolver resourceResolver) throws RepositoryException {
        final String queryString = String.format(
          "SELECT * FROM [cq:Page] AS page WHERE ISDESCENDANTNODE(page, '%s') AND [page].[%s] = '%s'", queryRootPath, propertyName, propertyValue);
        final Session session = resourceResolver.adaptTo(Session.class);
        return getQueryNodes(session, queryString);
    }

    public static NodeIterator searchByMultiplePropertiesWithOrCondition(final String queryRootPath, final List<Pair<String, String>> properties, final ResourceResolver resourceResolver) throws RepositoryException {
        int count = 0;
        final StringBuilder query = new StringBuilder();
        query.append(String.format("SELECT * FROM [cq:Page] AS page WHERE ISDESCENDANTNODE(page, '%s')", queryRootPath));
        if (!properties.isEmpty()) {
            query.append(" AND (");
            for (final Pair<String, String> pair : properties) {
                if (count != 0) {
                    query.append(" OR ");
                }
                query.append(String.format("[page].[%s] = '%s'", pair.getKey(), pair.getValue()));
                count++;
            }
            query.append(')');
        }
        final Session session = resourceResolver.adaptTo(Session.class);

        return getQueryNodes(session, query.toString());
    }

    public static NodeIterator searchByCalendarPropertyBefore(final String queryRootPath, final String primaryType, final String propertyName, final ZonedDateTime maxDate, final ResourceResolver resourceResolver) throws RepositoryException {
        final String queryString = String.format("select * from [%s] AS [s] where (ISDESCENDANTNODE([s], [%s])) AND ([s].[%s] < CAST('%s' AS DATE))",
          primaryType, queryRootPath, propertyName, maxDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        final Session session = resourceResolver.adaptTo(Session.class);
        return getQueryNodes(session, queryString);
    }

}
