package com.yunseul.optimization.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne
    @JoinColumn(name = "delivery_num")
    private Delivery delivery;

    private String name;
    private String phone;
    private String address;
    private String addressDetail;
    private Integer zip;
    private String doorLockPassword;

    private String productName;
    private Integer productAmount;
    private String productOption;
    private String safePhone;

    private boolean productDeliveryStatus;
    private LocalDateTime productDeliveryArriveTime;

    private Double latitude;
    private Double longitude;
}