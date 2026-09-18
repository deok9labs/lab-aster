package com.deok9labs.aster.ember.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/** {@code availability_submissions}의 복합 기본키다. */
@Embeddable
public class AvailabilitySubmissionsId implements Serializable {

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "week_start")
    private LocalDate weekStart;

    protected AvailabilitySubmissionsId() {
    }

    public AvailabilitySubmissionsId(Integer memberId, LocalDate weekStart) {
        this.memberId = memberId;
        this.weekStart = weekStart;
    }

    public Integer getMemberId() {
        return memberId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AvailabilitySubmissionsId that)) {
            return false;
        }
        return Objects.equals(memberId, that.memberId) && Objects.equals(weekStart, that.weekStart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, weekStart);
    }
}
