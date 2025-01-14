package com.yunseul.optimization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 주소 리스트를 기반으로 최적 경로를 계산하고 반환하는 서비스.
 *
 * 주요 기능:
 * - 주소를 좌표로 변환
 * - 거리 행렬을 생성하여 TSP 최적 경로 계산
 * - 주소와 좌표를 함께 포함한 정렬된 경로 반환
 */
@Service
@RequiredArgsConstructor
public class OptimalRouteService {

    private final GeocodingApiClient geocodingApiClient;
    private final DistanceMatrixService distanceMatrixService;
    private final RouteOptimizationService routeOptimizationService;

    /**
     * 주소 리스트를 기반으로 최적 경로를 계산합니다.
     *
     * @param addresses 주소 리스트
     * @return 최적 경로 정보 (정렬된 좌표와 인덱스 순서)
     */
    public Map<String, Object> calculateOptimalRoute(List<String> addresses) {

        // 1. 주소 → 좌표 변환
        List<double[]> coordinates = new ArrayList<>();
        for (String address : addresses) {
            coordinates.add(geocodingApiClient.getGeocode(address));
        }

        if (coordinates.size() < 2) {
            throw new IllegalArgumentException("최소 2개 이상의 좌표가 필요합니다.");
        }

        // 2. Distance Matrix 생성
        int[][] distanceMatrix = distanceMatrixService.buildDistanceMatrix(coordinates);

        // 3. TSP 최적화
        int[] optimalOrder = routeOptimizationService.optimizeRoute(distanceMatrix);

        // 4. 실제 경로 좌표 재구성
        List<double[]> orderedCoordinates = new ArrayList<>();
        for (int idx : optimalOrder) {
            orderedCoordinates.add(coordinates.get(idx));
        }

        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("optimalOrder", optimalOrder); // 인덱스 순서
        result.put("orderedCoordinates", orderedCoordinates); // 정렬된 좌표
        return result;
    }
}
