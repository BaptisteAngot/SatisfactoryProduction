package com.satisfactory.production.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TupleOrder {
    private Integer deep;
    private Recipe recipe;

    public TupleOrder(Integer deep, Recipe recipe) {
        this.deep = deep;
        this.recipe = recipe;
    }
}
