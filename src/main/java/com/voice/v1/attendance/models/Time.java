package com.voice.v1.attendance.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Time {

    String joinDateTime;
    String leaveDateTime;
}