package com.nbu.annotationplus.persistence.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Clock;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity {

    @Column(name = "created_ts")
    private LocalDateTime createdTs = LocalDateTime.now(Clock.systemUTC());

    @Column(name = "updated_ts")
    private LocalDateTime updatedTs;

    public LocalDateTime getCreatedTs() {
        return createdTs;
    }

    public LocalDateTime getUpdatedTs() {
        return updatedTs;
    }

    public void setCreatedTs(LocalDateTime createdTs) {
        this.createdTs = createdTs;
    }

    public void setUpdatedTs(LocalDateTime updatedTs) {
        this.updatedTs = updatedTs;
    }
}
