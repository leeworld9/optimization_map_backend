package com.yunseul.optimization.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 두 좌표 간 거리 행렬(2차원 배열)을 생성하는 서비스.
 *
 * 주요 기능:
 * - Haversine 공식을 활용하여 좌표 간 거리 계산
 * - 배송지 리스트에 대한 거리 행렬 생성
 */
@Service
public class DistanceMatrixService {

    private static final double EARTH_RADIUS = 6371; // 지구 반지름(km)

    /**
     * 배송지들(좌표 리스트)에 대한 거리 행렬(2차원 배열)을 생성합니다.
     *
     * @param coordinates  예: {[36.123, 127.456], [37.111, 127.999], ...}
     * @return             거리 행렬 (km 단위)
     */
    public int[][] buildDistanceMatrix(List<double[]> coordinates) {
        int size = coordinates.size();
        int[][] distanceMatrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    double distance = calculateHaversine(
                            coordinates.get(i)[0],
                            coordinates.get(i)[1],
                            coordinates.get(j)[0],
                            coordinates.get(j)[1]
                    );
                    distanceMatrix[i][j] = (int) Math.round(distance);
                }
            }
        }
        return distanceMatrix;
    }

    /**
     * 두 좌표(위도/경도) 간의 직선 거리를 Haversine 공식으로 계산합니다.
     */
    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c; // km 단위 거리
    }
}
