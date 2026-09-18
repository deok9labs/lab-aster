package com.deok9labs.aster.ember.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/** {@code availability_slots}의 복합 기본키다. */
@Embeddable
public class AvailabilitySlotsId implements Serializable {

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "week_start")
    private LocalDate weekStart;

    @Column(name = "available_date")
    private LocalDate availableDate;

    @Column(name = "slot_time")
    private LocalTime slotTime;

    protected AvailabilitySlotsId() {
    }

    public AvailabilitySlotsId(
            Integer memberId,
            LocalDate weekStart,
            LocalDate availableDate,
            LocalTime slotTime) {
        this.memberId = memberId;
        this.weekStart = weekStart;
        this.availableDate = availableDate;
        this.slotTime = slotTime;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public LocalTime getSlotTime() {
        return slotTime;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AvailabilitySlotsId that)) {
            return false;
        }
        return Objects.equals(memberId, that.memberId)
                && Objects.equals(weekStart, that.weekStart)
                && Objects.equals(availableDate, that.availableDate)
                && Objects.equals(slotTime, that.slotTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, weekStart, availableDate, slotTime);
    }
}
