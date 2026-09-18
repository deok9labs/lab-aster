package com.deok9labs.aster.ember.adapter.out.persistence.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** {@code availability_slots} 테이블의 JPA 매핑이다. */
@Entity
@Table(name = "availability_slots")
public class AvailabilitySlotsEntity {

    @EmbeddedId
    private AvailabilitySlotsId id;

    protected AvailabilitySlotsEntity() {
    }

    public AvailabilitySlotsEntity(AvailabilitySlotsId id) {
        this.id = id;
    }

    public AvailabilitySlotsId getId() {
        return id;
    }
}
