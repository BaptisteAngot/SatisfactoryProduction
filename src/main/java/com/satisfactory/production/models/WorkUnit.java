package com.satisfactory.production.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class WorkUnit {
    @NotNull
    private int id;

    @NotNull
    private String code;

    @NotNull
    private String libelle;

    public WorkUnit(int id, String code, String libelle) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
    }
}
