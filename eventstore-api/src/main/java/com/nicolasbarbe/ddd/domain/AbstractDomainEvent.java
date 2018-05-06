package com.nicolasbarbe.ddd.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use=JsonTypeInfo.Id.NAME,
        include=JsonTypeInfo.As.PROPERTY, property="type")
public abstract class AbstractDomainEvent  {
    private int version;

    public AbstractDomainEvent(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
