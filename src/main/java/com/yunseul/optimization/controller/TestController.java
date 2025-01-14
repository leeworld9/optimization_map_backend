package com.yunseul.optimization.controller;

import com.yunseul.optimization.dto.RouteResponseDto;
import com.yunseul.optimization.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

    private final GeocodingApiClient geocodingApiClient;
    private final DistanceMatrixService distanceMatrixService;
    private final RouteOptimizationService routeOptimizationService;
    private final OptimalRouteService optimalRouteService;
    private final RoadRouteService roadRouteService;

    String address = "대전 서구 대덕대로 211 갤러리아 타임월드";

    // 추루 리스트로 변경
    List<String> addresses = List.of(
            "대전 서구 갈마동 396-2",
            "대전 서구 탄방동 673",
            "대전 유성구 교촌동 639-12",
            "대전 서구 둔산동 1361",
            "대전 동구 홍도동 66-6",
            "대전 유성구 원내동 93-12",
            "대전 유성구 지족동 910-21",
            "대전 동구 가오동 647",
            "대전 중구 대흥동 497",
            "대전 중구 중촌동 16-8",
            "대전 동구 용전동 111-27"
    );

    @GetMapping("/geocoding")
    public ResponseEntity<?> testGeocoding() {
        double[] result = geocodingApiClient.getGeocode(address);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/route")
    public ResponseEntity<?>  testOptimizeRoute() {

        // 최적 경로 계산
        Map<String, Object> result = optimalRouteService.calculateOptimalRoute(addresses);

        /*
        각 루트의 주소를 다시 찾기에는 백엔드의 부하가 너무 큼
        key,value => String, List<double[]> 형태로 받아오는게 좋을거 같음.
         */

        return ResponseEntity.ok(result);
    }

    @GetMapping("/directions")
    public ResponseEntity<?> getRoadRoute() {

        @SuppressWarnings("unchecked")
        List<double[]> coords = (List<double[]>) optimalRouteService.calculateOptimalRoute(addresses).get("orderedCoordinates");
        if (coords.size() < 2) {
            return ResponseEntity.badRequest().body("At least 2 points required");
        }

        // RouteService에서 경로 계산 수행
        List<double[]> resultPath = roadRouteService.calculateRoute(coords);

        /*
        지도 기반의 안내는 해주지만 출발지, 경유지, 목적지 구분이 없음.
         */

        return ResponseEntity.ok(new RouteResponseDto(resultPath));
    }

}
