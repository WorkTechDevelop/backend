package com.example.work_task.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum Gender {
    /** Водительское удостоверение */
    MALE("MALE"),
    FEMALE("FEMALE");

    private final String value;
}
