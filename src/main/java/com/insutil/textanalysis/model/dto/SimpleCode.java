package com.insutil.textanalysis.model.dto;

import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class SimpleCode {
    Long id;
    String codeId;
    String codeName;
    boolean active;
    Long parentId;
}
