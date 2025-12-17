package com.yunseul.optimization.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private Double latitude;
    private Double longitude;
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "delivery_num")
    private Delivery delivery;
}