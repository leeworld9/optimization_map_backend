package com.yunseul.optimization.service;

import com.google.ortools.constraintsolver.*;
import org.springframework.stereotype.Service;

/**
 * OR-Tools를 사용하여 TSP 최적 경로를 계산하는 서비스.
 *
 * 주요 기능:
 * - 거리 행렬을 기반으로 TSP 최적화 수행
 * - 방문 순서를 반환하여 경로 최적화
 */
@Service
public class RouteOptimizationService {

    // ortools-darwin-aarch64-9.11.4210.jar 파일 구조로 인해 라이브러리를 가져오지 못해서 직접 추가함 (임시)
    static {
        System.load("/Users/dohelee/IdeaProjects/optimization/libs/libjniortools.dylib");
    }

    /**
     * OR-Tools를 사용하여 TSP 최적화를 수행하고, 방문 순서를 반환합니다.
     *
     * @param distanceMatrix 각 지점 간 거리(비용) 정보
     * @return               방문 순서를 담은 List<Integer> (예: [0, 2, 1, 3])
     */
    public int[] optimizeRoute(int[][] distanceMatrix) {
        int numLocations = distanceMatrix.length;
        // 차량 수와 시작점
        int vehicleNumber = 1;
        int depot = 0; // 0번 인덱스를 출발 지점으로 가정

        // Manager 생성
        RoutingIndexManager manager = new RoutingIndexManager(numLocations, vehicleNumber, depot);

        // Routing Model 생성
        RoutingModel routing = new RoutingModel(manager);

        // 비용(거리) 콜백 등록
        int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            int toNode = manager.indexToNode(toIndex);
            return distanceMatrix[fromNode][toNode];
        });

        // 모든 차량에 대해 거리(비용) 설정
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // 탐색 파라미터 설정
        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .build();

        // 문제 풀기
        Assignment solution = routing.solveWithParameters(searchParameters);

        if (solution != null) {
            return getRoute(solution, routing, manager);
        } else {
            // 최적 경로를 찾지 못한 경우 처리 로직
            return new int[0];
        }
    }

    /**
     * OR-Tools 솔루션에서 최적 경로를 추출합니다.
     */
    private int[] getRoute(Assignment solution, RoutingModel routing, RoutingIndexManager manager) {
        int routeSize = manager.getNumberOfNodes();
        int[] route = new int[routeSize];
        int index = (int) routing.start(0); // vehicle index == 0
        int i = 0;

        while (!routing.isEnd(index)) {
            int nodeIndex = manager.indexToNode(index);
            route[i++] = nodeIndex;
            index = (int) solution.value(routing.nextVar(index));
        }
        // route 배열에는 시작점(Depot) ~ 최종점(Depot) 순서가 들어가지만,
        // isEnd(index)에 도달하면 Depot(마지막) 노드를 추가하지 않은 상태일 수 있으므로
        // 필요 시 마지막 Depot 인덱스도 포함하거나 생략할 수 있음.

        return route;
    }
}
