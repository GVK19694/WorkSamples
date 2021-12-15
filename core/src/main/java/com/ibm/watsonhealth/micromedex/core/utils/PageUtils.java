package com.ibm.watsonhealth.micromedex.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * @author Andreas Kaltseis
 *
 */
public final class PageUtils {

    private PageUtils() {
    }

    public static Optional<String> getPageTitleFromPath(@NotNull final String path, @NotNull final ResourceResolver resourceResolver) {
        Optional<String> title = Optional.empty();
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        final Page currentPage = pageManager.getContainingPage(path);

        if (currentPage != null) {
            title = Optional.of(currentPage.getTitle());
        }
        return title;
    }

    public static List<String> getPageTitlesFromPathList(final List<String> paths, @NotNull final ResourceResolver resourceResolver) {
        final List<String> titles = new ArrayList<>();
        if (paths != null) {
            for (final String path : paths) {
                getPageTitleFromPath(path, resourceResolver).ifPresent(titles::add);
            }
        }
        return titles;
    }

}
