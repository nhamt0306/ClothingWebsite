package com.example.clothingstore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DateForm {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
