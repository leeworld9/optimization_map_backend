package com.yunseul.optimization.entity;
import com.yunseul.optimization.dto.enumClass.MemeberStatus;
import com.yunseul.optimization.dto.enumClass.MemeberType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 50)
    private String pw;

    @Enumerated(EnumType.STRING)
    private MemeberType type;

    private String company;

    @Column(length = 50)
    private String phone;

    @Column(length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private MemeberStatus status;
}