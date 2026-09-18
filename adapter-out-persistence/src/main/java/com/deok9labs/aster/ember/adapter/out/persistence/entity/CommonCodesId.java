package com.deok9labs.aster.ember.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/** {@code common_codes}의 복합 기본키다. */
@Embeddable
public class CommonCodesId implements Serializable {

    @Column(name = "code_group", length = 50)
    private String codeGroup;

    @Column(name = "code", length = 50)
    private String code;

    protected CommonCodesId() {
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CommonCodesId that)) {
            return false;
        }
        return Objects.equals(codeGroup, that.codeGroup) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codeGroup, code);
    }
}
