package com.yunseul.optimization.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 네이버 Geocoding API를 사용하여 주소를 좌표로 변환하는 API 클라이언트.
 *
 * 주요 기능:
 * - 입력된 주소 문자열을 기반으로 위도와 경도를 반환
 * - Geocoding API 호출 및 응답 처리
 */
@Service
public class GeocodingApiClient {
    private static final String NAVER_API_URL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";

    @Value("${NAVER.API.MAPS.CLIENT.ID}")
    private String MAPS_API_CLIENT_ID;
    @Value("${NAVER.API.MAPS.CLIENT.SECRET}")
    private String MAPS_API_CLIENT_SECRET;

    public double[] getGeocode(String address) {
        RestTemplate restTemplate = new RestTemplate();
        String url = NAVER_API_URL + "?query=" + address;

        // 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", MAPS_API_CLIENT_ID);
        headers.set("X-NCP-APIGW-API-KEY", MAPS_API_CLIENT_SECRET);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map body = response.getBody();
            List<Map<String, Object>> addresses = (List<Map<String, Object>>) body.get("addresses");
            if (!addresses.isEmpty()) {
                Map<String, Object> firstResult = addresses.get(0);
                double latitude = Double.parseDouble(firstResult.get("y").toString());
                double longitude = Double.parseDouble(firstResult.get("x").toString());
                return new double[]{latitude, longitude};
            }
        }
        throw new RuntimeException("Failed to get coordinates for address: " + address);
    }
}
