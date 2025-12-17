package com.yunseul.optimization.controller;

import com.yunseul.optimization.dto.NaverDirectionsOptionEnum;
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

    // 추후 리스트로 변경
    List<String> addresses = List.of(
            "대전 서구 갈마동 396-2",
            "대전 서구 탄방동 673",
//            "대전 유성구 교촌동 639-12",
            "대전 서구 둔산동 1361",
            "대전 서구 갈마동 394-1",
            "대전 서구 갈마동 343-17"
//            "대전 동구 홍도동 66-6",
//            "대전 유성구 원내동 93-12",
//            "대전 유성구 지족동 910-21",
//            "대전 동구 가오동 647",
//            "대전 중구 중촌동 16-8",
//            "대전 동구 용전동 111-27",
//            "대전 동구 용전동 111-27",
//            "대전 서구 정림동 641-1",
//            "대전 서구 정림동 633",
//            "대전 서구 정림동 716",
//            "대전 중구 목동 24-14",
//            "대전 중구 문화동 10-7",
//            "대전 중구 문화동 779",
//            "대전 유성구 덕명동 16-1",
//            "대전 유성구 계산동 721",
//            "대전 중구 대흥동 497"
    );

    // ADDRESS, ADDRESS DETAIL
    // 같은 주소인데 동호수만 다른 경우??
    // 완전히 같은 주소인데 물건이 여러개 배송될 경우??
        // 그러면 한번만 있으면 되짆아 하나의 주소에 여러개 등록하도록 해야하나?
        // 시퀀스를 구분하면은....? 음 고민좀 해보자...
    // 프론트에 다 뿌릴필요 있나? 정렬된 인덱스랑 경로만 뿌리면되는거 아닌가???
    // db 저장해야할 필요가 있으니까? 시퀀스번호도 저장해야하나...?
    // db에 저장할때 위도 경도 같이 저장하면 로딩속도 최적화 될꺼 같은디...

    // 고려해야될 사항이 겁나 많네;
//    List<List<String>> addresses = List.of(
//            List.of("대전 서구 갈마동 396-2", "빌딩 01호"),
//            List.of("대전 서구 탄방동 673", "빌딩 02호"),
//            List.of("대전 유성구 교촌동 639-12", "빌딩 03호"),
//            List.of("대전 서구 둔산동 1361", "빌딩 04호"),
//            List.of("대전 동구 홍도동 66-6", "빌딩 05호"),
//            List.of("대전 유성구 원내동 93-12", "빌딩 06호"),
//            List.of("대전 유성구 지족동 910-21", "빌딩 07호"),
//            List.of("대전 동구 가오동 647", "빌딩 08호"),
//            List.of("대전 중구 중촌동 16-8", "빌딩 09호"),
//            List.of("대전 동구 용전동 111-27", "빌딩 10호"),
//            List.of("대전 동구 용전동 111-27", "빌딩 11호"),
//            List.of("대전 서구 정림동 641-1", "빌딩 12호"),
//            List.of("대전 서구 정림동 633", "빌딩 13호"),
//            List.of("대전 서구 정림동 716", "빌딩 14호"),
//            List.of("대전 중구 목동 24-14", "빌딩 15호"),
//            List.of("대전 중구 문화동 10-7", "빌딩 16호"),
//            List.of("대전 중구 문화동 779", "빌딩 17호"),
//            List.of("대전 유성구 덕명동 16-1", "빌딩 18호"),
//            List.of("대전 유성구 계산동 721", "빌딩 19호"),
//            List.of("대전 중구 대흥동 497", "빌딩 20호")
//    );

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
        아닌가?? directions 할때 어떻게 중간중간에 넣을지 고민해보고 생각해야겠다.
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
        Map<String, Object> resultPath = roadRouteService.calculateRoute(coords, NaverDirectionsOptionEnum.FAST);

        /*
        지도 기반의 안내는 해주지만 출발지, 경유지, 목적지 구분이 없음.
         */

        return ResponseEntity.ok(resultPath);
    }

    @GetMapping("/down")
    public String testDown() {
        return "<html>" +
                "<a href='https://drive.google.com/file/d/1PtiqanI9MN4fbXt5r7G0NtiRet4iMPBY/view?usp=drive_link'>DOWN</a>"+
                "<html>";
    }

}
