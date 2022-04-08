package com.satisfactory.production.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class Article {
    @NotNull
    private int id;

    @NotNull
    private String code;

    @NotNull
    private String libelle;

    @NotNull
    private int id_categorie;

    public Article(int id, String code, String libelle, int id_categorie) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.id_categorie = id_categorie;
    }
}
