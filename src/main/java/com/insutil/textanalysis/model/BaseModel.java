package com.insutil.textanalysis.model;

import java.time.LocalDateTime;

public abstract class BaseModel {
    protected Long registUser;
    protected Long updateUser;
    protected LocalDateTime registDate;
    protected LocalDateTime updateDate;
}
