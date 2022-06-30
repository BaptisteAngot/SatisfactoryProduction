package com.satisfactory.production.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TupleOrder {
    private Integer deep;
    private Integer qty;

    public TupleOrder(Integer deep, Integer qty) {
        this.deep = deep;
        this.qty = qty;
    }
}
