package com.deok9labs.aster.ember.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** {@code availability_submissions} 테이블의 JPA 매핑이다. */
@Entity
@Table(name = "availability_submissions")
public class AvailabilitySubmissionsEntity {

    @EmbeddedId
    private AvailabilitySubmissionsId id;

    @Column(name = "revision", nullable = false)
    private int revision;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected AvailabilitySubmissionsEntity() {
    }

    public AvailabilitySubmissionsEntity(
            AvailabilitySubmissionsId id,
            int revision,
            LocalDateTime updatedAt) {
        this.id = id;
        this.revision = revision;
        this.updatedAt = updatedAt;
    }

    public AvailabilitySubmissionsId getId() {
        return id;
    }

    public int getRevision() {
        return revision;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void advanceRevision(LocalDateTime changedAt) {
        revision += 1;
        updatedAt = changedAt;
    }
}
