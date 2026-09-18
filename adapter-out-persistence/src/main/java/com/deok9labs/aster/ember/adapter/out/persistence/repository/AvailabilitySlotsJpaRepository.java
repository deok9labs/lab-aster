package com.deok9labs.aster.ember.adapter.out.persistence.repository;

import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySlotsEntity;
import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySlotsId;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailabilitySlotsJpaRepository
        extends JpaRepository<AvailabilitySlotsEntity, AvailabilitySlotsId> {

    List<AvailabilitySlotsEntity> findAllByIdWeekStartOrderByIdAvailableDateAscIdSlotTimeAsc(
            LocalDate weekStart);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from AvailabilitySlotsEntity slot
            where slot.id.memberId = :memberId and slot.id.weekStart = :weekStart
            """)
    // 전체 교체 전에 bulk delete를 즉시 실행해 같은 복합키를 다시 넣어도 충돌하지 않게 한다.
    void deleteMemberWeekSlots(
            @Param("memberId") int memberId,
            @Param("weekStart") LocalDate weekStart);
}
