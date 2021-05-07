package com.hemant.vaccine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Slot {
    private String date;
    private int age;
    private String vaccine;
    private String center;
    private String block;
    private int pin;
    private int slotsAvailable;
}
