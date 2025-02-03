package com.yunseul.optimization.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(of = "value") // toString() 호출 시 value 값을 반환
public enum NaverDirectionsOptionEnum {
    /** 실시간 빠른 길 */
    FAST("trafast"),

    /** 실시간 편한 길 */
    COMPORT("tracomfort"),

    /** 실시간 최적 */
    OPTIMAL("traoptimal"),

    /** 무료 우선 */
    AVOIDTOLL("traavoidtoll"),

    /** 자동차 전용 도로 회피 우선 */
    AVOIDCARONLY("traavoidcaronly")
    ;

    private final String value; // 필드 추가

    NaverDirectionsOptionEnum(String value) {
        this.value = value;
    }
}
