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
    private int quantite;

    @NotNull
    private int numOrder;

    public OperationOrder(String codeArticle, int quantite, int numOrder) {
        this.codeArticle = codeArticle;
        this.quantite = quantite;
        this.numOrder = numOrder;
    }
}
