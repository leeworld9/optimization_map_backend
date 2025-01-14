package com.yunseul.optimization.dto;

import lombok.Data;

import java.util.List;

@Data
public class RouteRequestDto {
    private List<double[]> coordinates;
}