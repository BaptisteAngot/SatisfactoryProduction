package com.satisfactory.production.models;

import com.sun.istack.internal.NotNull;
import lombok.Data;

@Data
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
}
