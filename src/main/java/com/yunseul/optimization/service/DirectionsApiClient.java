package com.yunseul.optimization.service;

import com.yunseul.optimization.dto.NaverDirectionsOptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * 네이버 Directions 15 API와 통신하여 도로 기반 경로 데이터를 가져오는 API 클라이언트.
 *
 * 주요 기능:
 * - 출발지, 목적지, 경유지를 기반으로 최적 경로를 조회
 * - Directions 15 API 호출 및 응답 처리
 */
@Service
@RequiredArgsConstructor
public class DirectionsApiClient {

    private static final String DIRECTIONS_URL = "https://naveropenapi.apigw.ntruss.com/map-direction-15/v1/driving";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${NAVER.API.MAPS.CLIENT.ID}")
    private String clientId;
    @Value("${NAVER.API.MAPS.CLIENT.SECRET}")
    private String clientSecret;

    /**
     * 네이버 Directions 15 API를 호출하여 도로 경로 및 기타 정보를 가져옵니다.
     *
     * @param start     출발지 좌표 (lat, lng 순)
     * @param end       목적지 좌표 (lat, lng 순)
     * @param waypoints 경유지 좌표 리스트 (lat, lng 순)
     * @return API 응답 본문 (Map 형태)
     * @throws RuntimeException API 호출 실패 시 예외 발생
     */
    public Map<String, Object> getRoadPath(double[] start, double[] end, List<double[]> waypoints, NaverDirectionsOptionEnum option) {
        // 1) start, end, waypoints 파라미터 변환
        String startParam = String.format("%f,%f", start[1], start[0]);
        String goalParam = String.format("%f,%f", end[1], end[0]);

        StringBuilder wps = new StringBuilder();
        for (int i = 0; i < waypoints.size(); i++) {
            double[] waypoint = waypoints.get(i);
            wps.append(String.format("%f,%f", waypoint[1], waypoint[0]));
            if (i < waypoints.size() - 1) {
                wps.append("|");
            }
        }

        // 2) API 호출 URL 생성
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(DIRECTIONS_URL)
                .queryParam("start", startParam)
                .queryParam("goal", goalParam)
                .queryParam("option", option.getValue());

        if (!waypoints.isEmpty()) {
            builder.queryParam("waypoints", wps.toString());
        }

        // 3) 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 4) API 호출
        ResponseEntity<Map> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to call Directions API: " + response.getStatusCode());
        }
    }
}