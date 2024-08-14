package com.entity;

import lombok.Data;

@Data
public class MsgA {
    private String msg;
    private Coordinates coordinates;
    private LngEnum lng;

    public enum LngEnum {
        RU
    }

}
