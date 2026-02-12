package com.codeit.async.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String coffeeType;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String status;  // PENDING, COMPLETED, FAILED

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 생성자
    public Order(String userId, String coffeeType, Integer price) {
        this.userId = userId;
        this.coffeeType = coffeeType;
        this.price = price;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    // 상태 변경 메서드
    public void complete() {
        this.status = "COMPLETED";
    }

    public void fail() {
        this.status = "FAILED";
    }

}
