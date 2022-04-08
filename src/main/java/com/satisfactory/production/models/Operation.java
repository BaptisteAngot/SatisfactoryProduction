package com.satisfactory.production.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class Operation {
    @NotNull
    private int id;

    @NotNull
    private String code;

    @NotNull
    private String libelle;

    @NotNull
    private int delai;

    @NotNull
    private int delaiInstallation;

    public Operation(int id, String code, String libelle, int delai, int delaiInstallation) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.delai = delai;
        this.delaiInstallation = delaiInstallation;
    }
}
