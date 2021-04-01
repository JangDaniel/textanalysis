package com.insutil.textanalysis.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class ContractObject {
    private Long id;
    private Long sttContentsId;
    private String objectCode;
    private String objectName;
    private String objectValue;
    private String legacyObjectCode;
    private Long registUser;
    private Long updateUser;
    private LocalDateTime registDate;
    private LocalDateTime updateDate;

    public ContractObject update(ContractObject data) {
        if(data.sttContentsId != null) this.sttContentsId = data.sttContentsId;
        if(data.objectCode != null) this.objectCode = data.objectCode;
        if(data.objectName != null) this.objectName = data.objectName;
        if(data.objectValue != null) this.objectValue = data.objectValue;
        if(data.legacyObjectCode != null) this.legacyObjectCode = data.legacyObjectCode;
        if(data.updateUser != null) this.updateUser = data.updateUser;
        return this;
    }
}