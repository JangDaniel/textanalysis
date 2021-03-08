package com.insutil.textanalysis.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_ins_code")
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id"})
public class Code {
    @Id
    private Long id;
    private String codeId;
    private String codeName;
    private Boolean active;
    private Long parentId;
    private Long registUser;
    private Long updateUser;
    private LocalDateTime registDate;
    private LocalDateTime updateDate;
    private Boolean enabled;

    public Code update(Code code) {
        if (code.getCodeId() != null) this.setCodeId(code.getCodeId());
        if (code.getCodeName() != null) this.setCodeName(code.getCodeName());
        if (code.getParentId() != null) this.setParentId(code.getParentId());
        if (code.getActive() != null) this.setActive(code.getActive());
        if (code.getEnabled() != null) this.setEnabled(code.getEnabled());
        if (code.getUpdateUser() != null) this.setUpdateUser(code.getUpdateUser());

        return this;
    }
}