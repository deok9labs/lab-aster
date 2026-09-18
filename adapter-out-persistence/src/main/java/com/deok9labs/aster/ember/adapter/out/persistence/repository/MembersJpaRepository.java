package com.deok9labs.aster.ember.adapter.out.persistence.repository;

import com.deok9labs.aster.ember.adapter.out.persistence.entity.MembersEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembersJpaRepository extends JpaRepository<MembersEntity, Integer> {

    @Query("""
            select member
            from MembersEntity member, CommonCodesEntity position
            where member.activeCodeGroup = 'ACTIVE_STATUS'
              and member.activeCode = 'ACTIVE'
              and position.id.codeGroup = member.positionCodeGroup
              and position.id.code = member.positionCode
              and position.enabled = true
            order by position.sortOrder, member.id
            """)
    // 표시 순서는 코드 값 자체가 아니라 운영자가 관리하는 공통 코드 순서를 따른다.
    List<MembersEntity> findActiveMembersInDisplayOrder();

    @Query("""
            select (count(member) > 0)
            from MembersEntity member
            where member.id = :memberId
              and member.activeCodeGroup = 'ACTIVE_STATUS'
              and member.activeCode = 'ACTIVE'
            """)
    boolean existsActiveMember(@Param("memberId") int memberId);
}
