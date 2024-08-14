package com.entity;

import lombok.Data;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class MsgB {
    private String txt;
    private String createdDt;
    private String currentTemp;

    public MsgB() {
        this.createdDt = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }
}
