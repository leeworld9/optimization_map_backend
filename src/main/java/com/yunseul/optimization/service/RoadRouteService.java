package com.yunseul.optimization.service;

import com.yunseul.optimization.dto.NaverDirectionsOptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 도로 기반 경로 탐색 서비스
 */
@Service
@RequiredArgsConstructor
public class RoadRouteService {

    private final DirectionsApiClient directionsApiClient;

    /**
     * 최적 경로 계산
     */
    public Map<String, Object> calculateRoute(List<double[]> coords, NaverDirectionsOptionEnum option) {
        if (coords.size() <= 2) {
            return callDirectionsApi(coords.get(0), coords.get(1), Collections.emptyList(), option);
        }

        double[] start = coords.get(0);
        double[] end = coords.get(coords.size() - 1);
        List<double[]> waypoints = coords.subList(1, coords.size() - 1);

        List<Map<String, Object>> responses;

        if (waypoints.size() > 15) {
            responses = handleChunkedWaypoints(start, end, waypoints, option);
        } else {
            responses = List.of(callDirectionsApi(start, end, waypoints, option));
        }

        return mergeResponses(responses, option);
    }

    /**
     * 네이버 Directions API 호출 래퍼
     */
    private Map<String, Object> callDirectionsApi(double[] start, double[] end, List<double[]> waypoints, NaverDirectionsOptionEnum option) {
        return directionsApiClient.getRoadPath(start, end, waypoints, option);
    }

    /**
     * 경유지가 15개 초과일 경우 API 요청을 분할하여 실행
     */
    private List<Map<String, Object>> handleChunkedWaypoints(double[] start, double[] end, List<double[]> waypoints, NaverDirectionsOptionEnum option) {
        final int BATCH = 15;
        List<Map<String, Object>> responses = new ArrayList<>();
        double[] currentStart = start;
        int index = 0;

        while (index < waypoints.size()) {
            int endIndex = Math.min(index + BATCH, waypoints.size());
            List<double[]> subWaypoints = new ArrayList<>(waypoints.subList(index, endIndex));
            boolean isLastBatch = (endIndex == waypoints.size());
            double[] currentEnd = isLastBatch ? end : waypoints.get(endIndex);

            responses.add(callDirectionsApi(currentStart, currentEnd, subWaypoints, option));

            currentStart = currentEnd;
            index = endIndex;
        }

        return responses;
    }

    /**
     * 여러 개의 API 응답을 병합하여 최적 경로 생성
     */
    private Map<String, Object> mergeResponses(List<Map<String, Object>> responses, NaverDirectionsOptionEnum option) {
        Map<String, Object> mergedResponse = new HashMap<>();
        List<List<Double>> mergedPath = new ArrayList<>();
        List<Map<String, Object>> mergedGuide = new ArrayList<>();
        int totalDuration = 0;
        int totalDistance = 0;
        int currentPointIndex = 0; // 전체 경로에서의 pointIndex

        List<Double> lastPoint = null;
        Map<String, Object> lastInstruction = null;

        for (int i = 0; i < responses.size(); i++) {
            Map<String, Object> response = responses.get(i);
            Map<String, Object> route = (Map<String, Object>) response.get("route");

            List<Map<String, Object>> routeList = (List<Map<String, Object>>) route.get(option.getValue());
            Map<String, Object> routeInfo = routeList.get(0);

            List<List<Double>> path = (List<List<Double>>) routeInfo.get("path");

            // 🔥 경도(lng), 위도(lat) 순서로 변환하여 저장
            List<List<Double>> correctedPath = new ArrayList<>();
            for (List<Double> coords : path) {
                if (coords.size() == 2) {
                    correctedPath.add(Arrays.asList(coords.get(1), coords.get(0))); // [위도, 경도] → [경도, 위도] 변환
                }
            }

            // 🔥 중복된 첫 번째 좌표 제거 (연결을 위해)
            if (lastPoint != null && lastPoint.equals(correctedPath.get(0))) {
                correctedPath.remove(0);
            }

            Map<String, Object> summary = (Map<String, Object>) routeInfo.get("summary");
            Integer duration = (Integer) summary.get("duration");
            Integer distance = (Integer) summary.get("distance");

            // 🔥 중복된 거리 및 시간 값 제거 (첫 번째 응답 제외)
            if (i > 0) {
                totalDuration += (duration - (Integer) lastInstruction.get("duration"));
                totalDistance += (distance - (Integer) lastInstruction.get("distance"));
            } else {
                totalDuration += duration;
                totalDistance += distance;
            }

            // 🔥 가이드(경로 안내) 리스트 가져오기
            List<Map<String, Object>> guide = (List<Map<String, Object>>) routeInfo.get("guide");

            // 🔥 중복된 "목적지" 가이드 제거 (첫 번째 응답 제외)
            if (i > 0 && lastInstruction != null && lastInstruction.get("instructions").toString().contains("목적지")) {
                mergedGuide.remove(mergedGuide.size() - 1);
            }

            // 🔥 pointIndex 값 재계산 (현재까지의 경로 길이를 기준으로 재정렬)
            for (Map<String, Object> guideStep : guide) {
                int originalIndex = (Integer) guideStep.get("pointIndex");
                guideStep.put("pointIndex", currentPointIndex + originalIndex);
            }

            mergedPath.addAll(correctedPath); // 🔥 순서가 수정된 경로 추가
            mergedGuide.addAll(guide);

            lastPoint = correctedPath.get(correctedPath.size() - 1);
            lastInstruction = mergedGuide.get(mergedGuide.size() - 1);

            // 🔥 다음 세그먼트를 위한 전체 index 업데이트
            currentPointIndex = mergedPath.size();
        }

        return Map.of("route", Map.of(
                "path", mergedPath,
                "guide", mergedGuide,
                "summary", Map.of("duration", totalDuration, "distance", totalDistance)
        ));
    }
}
