package com.codeit.async.model;

import lombok.*;

@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Coffee {

    private Long id;
    private String type;
    private String size;
    private int price;

    public Coffee(String type) {
        this.type = type;
        this.size = "MEDIUM";
        this.price = 4500;
    }
}
