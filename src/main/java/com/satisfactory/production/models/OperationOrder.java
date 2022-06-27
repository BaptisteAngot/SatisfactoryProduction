package com.satisfactory.production.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class OperationOrder {

    @NotNull
    private String codeArticle;

    @NotNull
    private int productQuantity;

    @NotNull
    private int order;

    public OperationOrder(String codeArticle, int productQuantity, int order) {
        this.codeArticle = codeArticle;
        this.productQuantity = productQuantity;
        this.order = order;
    }
}
