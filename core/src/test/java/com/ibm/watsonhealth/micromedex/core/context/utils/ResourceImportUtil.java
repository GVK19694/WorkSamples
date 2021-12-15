package com.ibm.watsonhealth.micromedex.core.context.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.mock.sling.loader.ContentLoader;

import com.adobe.granite.rest.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceImportUtil {

    private static final Comparator<String> fileFirstComparator = Comparator
      .<String, Boolean>comparing(file -> file.endsWith(Constants.EXT_JSON))
      .reversed();

    private ResourceImportUtil() {
    }

    public static void loadResources(final ContentLoader loader, final String resourcesPath) {
        try {
            final ClassLoader classLoader = ResourceImportUtil.class.getClassLoader();
            if (classLoader != null) {
                final InputStream stream = classLoader.getResourceAsStream(resourcesPath);
                if (stream != null) {
                    final List<String> files = IOUtils.readLines(stream, StandardCharsets.UTF_8);
                    files.stream().sorted(fileFirstComparator).forEach(file -> saveResourceFile(loader, resourcesPath, file));
                }
            }
        } catch (final IOException e) {
            log.error("Error during loading resources into context", e);
        }
    }

    private static void saveResourceFile(final ContentLoader loader, final String resourcesPath, final String file) {
        final String fullPath = getFullPath(resourcesPath, file);
        if (StringUtils.startsWith(fullPath, "jcr_root")) {
            if (isFile(fullPath)) {
                saveResourceInContext(loader, fullPath);
            } else if (isDirectory(fullPath)) {
                loadResources(loader, fullPath);
            }
        } else {
            loadResources(loader, fullPath);
        }
    }

    private static boolean isFile(final String path) {
        return path.endsWith(Constants.EXT_JSON);
    }

    private static boolean isDirectory(final String path) {
        return ResourceImportUtil.class.getClassLoader().getResource(path + "/") != null;
    }

    private static void saveResourceInContext(final ContentLoader loader, final String file) {
        final String classpathResource = "/" + file;
        final String destinationPath = StringUtils.removeStart(classpathResource.substring(0, classpathResource.lastIndexOf("/")), "/jcr_root");
        loader.json(classpathResource, destinationPath);
    }

    private static String getFullPath(final String previousPath, final String fileName) {
        return StringUtils.isNotEmpty(previousPath) ? (previousPath + "/" + fileName) : fileName;
    }

}