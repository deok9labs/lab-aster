package com.deok9labs.aster.ember.adapter.out.persistence.repository;

import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySubmissionsEntity;
import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySubmissionsId;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilitySubmissionsJpaRepository
        extends JpaRepository<AvailabilitySubmissionsEntity, AvailabilitySubmissionsId> {

    List<AvailabilitySubmissionsEntity> findAllByIdWeekStart(LocalDate weekStart);
}
