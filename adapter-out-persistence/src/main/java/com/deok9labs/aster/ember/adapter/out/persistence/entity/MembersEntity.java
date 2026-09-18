package com.deok9labs.aster.ember.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** {@code members} 테이블의 JPA 매핑이다. */
@Entity
@Table(name = "members")
public class MembersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "server_name", nullable = false, length = 100)
    private String serverName;

    @Column(name = "position_code_group", nullable = false, length = 50)
    private String positionCodeGroup;

    @Column(name = "position_code", nullable = false, length = 50)
    private String positionCode;

    @Column(name = "active_code_group", nullable = false, length = 50)
    private String activeCodeGroup;

    @Column(name = "active_code", nullable = false, length = 50)
    private String activeCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected MembersEntity() {
    }

    public Integer getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getServerName() {
        return serverName;
    }

    public String getPositionCode() {
        return positionCode;
    }
}
