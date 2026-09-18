package com.deok9labs.aster.ember.adapter.out.persistence.repository;

import com.deok9labs.aster.ember.adapter.out.persistence.entity.CommonCodesEntity;
import com.deok9labs.aster.ember.adapter.out.persistence.entity.CommonCodesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonCodesJpaRepository extends JpaRepository<CommonCodesEntity, CommonCodesId> {
}
