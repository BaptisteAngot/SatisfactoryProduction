package com.satisfactory.production.models;

import com.sun.istack.internal.NotNull;
import lombok.Data;

@Data
public class Recipe {
    @NotNull
    private int id_article;

    @NotNull
    private int id_operation;

    @NotNull
    private int id_composant1;

    @NotNull
    private int quantite1;

    private int id_composant2;
    private int quantite2;
}
