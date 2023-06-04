package com.example.clothingstore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class RevenueFrom2Day {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date from;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date to;
}
