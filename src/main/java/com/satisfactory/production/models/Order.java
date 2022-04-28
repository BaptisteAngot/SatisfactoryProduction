package com.satisfactory.production.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class Order {

    @NotNull
    private String codeWorkUnit;

    @NotNull
    private List<OperationOrder> operations;

    public Order(String codeWorkUnit, List<OperationOrder> operations) {
        this.codeWorkUnit = codeWorkUnit;
        this.operations = operations;
    }

    @Override
    public String toString() {
        return "Order{" +
                "codeWorkUnit='" + codeWorkUnit + '\'' +
                ", operations=" + operations +
                '}';
    }
}
