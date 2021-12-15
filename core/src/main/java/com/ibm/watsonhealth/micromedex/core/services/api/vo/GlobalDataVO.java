package com.ibm.watsonhealth.micromedex.core.services.api.vo;

import java.time.LocalDateTime;

public class GlobalDataVO {

    private final LocalDateTime lastModified;

    private final String lastModifiedBy;

    public GlobalDataVO(final LocalDateTime lastModified, final String lastModifiedBy) {
        this.lastModified = lastModified;
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModified() {
        return this.lastModified;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

}
