package com.yunseul.optimization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 도로 기반의 최적 경로를 계산하고 반환하는 서비스.
 *
 * 주요 기능:
 * - 출발지, 경유지, 목적지 간 도로 경로 계산
 * - Directions API 호출을 통해 도로 기반의 경로 반환
 * - 경유지가 15개를 초과할 경우 배치 처리
 */
@Service
@RequiredArgsConstructor
public class RoadRouteService {

    private final DirectionsApiClient directionsApiClient;

    /**
     * 주어진 좌표 리스트를 기반으로 출발지, 경유지, 도착지 까지의 도로 기반의 최적의 경로를 도출 합니다.
     *
     * @param coords 출발지, 경유지, 목적지가 포함된 좌표 리스트 (lat, lng 순)
     * @return 도로 기반 최적 경로 좌표 리스트
     */
    public List<double[]> calculateRoute(List<double[]> coords) {
        if (coords.size() <= 2) {
            // 출발~도착만 있으면 경유지가 없음
            return directionsApiClient.getRoadPath(coords.get(0), coords.get(1), Collections.emptyList());
        }

        // start / end / waypoints
        double[] start = coords.get(0);
        double[] end = coords.get(coords.size() - 1);
        List<double[]> waypoints = coords.subList(1, coords.size() - 1);

        // 경유지 15개 이하
        if (waypoints.size() <= 15) {
            return directionsApiClient.getRoadPath(start, end, waypoints);
        }
        // 경유지 15개 초과
        else {
            return handleChunkedWaypoints(start, end, waypoints);
        }
    }

    /**
     * 경유지가 15개를 초과할 경우, API 호출을 여러 번 나누어 수행하고 결과를 병합합니다.
     *
     * @param start     출발지 좌표 (lat, lng 순)
     * @param end       목적지 좌표 (lat, lng 순)
     * @param waypoints 경유지 좌표 리스트 (lat, lng 순)
     * @return 병합된 도로 기반 경로 좌표 리스트
     */
    private List<double[]> handleChunkedWaypoints(double[] start, double[] end, List<double[]> waypoints) {
        final int BATCH = 15;
        List<double[]> mergedPath = new ArrayList<>();
        double[] currentStart = start;
        int index = 0;

        while (index < waypoints.size()) {
            int endIndex = Math.min(index + BATCH, waypoints.size());
            List<double[]> subWaypoints = waypoints.subList(index, endIndex);
            boolean isLastBatch = (endIndex == waypoints.size());
            double[] currentEnd = isLastBatch ? end : waypoints.get(endIndex - 1);

            // Directions 호출
            List<double[]> partialPath = directionsApiClient.getRoadPath(currentStart, currentEnd, subWaypoints);

            // 병합 과정에서 중복 제거
            if (!mergedPath.isEmpty()) {
                partialPath.remove(0);
            }

            mergedPath.addAll(partialPath);

            // 다음 배치를 위해 start 업데이트
            currentStart = currentEnd;
            index = endIndex;
        }

        return mergedPath;
    }
}
