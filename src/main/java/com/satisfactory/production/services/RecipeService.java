package com.satisfactory.production.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satisfactory.production.models.Recipe;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private static final String URLBASE = "http://88.168.248.140:8000/";
    private static final String URL_RECIPES = "recipes/";
    private static final String URL_RECIPE = "recipe/";

    public List<Recipe> getAllService() throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_RECIPES)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            List<Recipe> recipes = new ArrayList<>();
            for (final JsonNode objNode : json) {
                int id_article = objNode.get("id_article").asInt();
                int id_operation = objNode.get("id_operation").asInt();
                int id_composant1 = objNode.get("id_composant1").asInt();
                int quantite1 = objNode.get("quantite1").asInt();

                Optional<Integer> opt_id_composant2 = Optional.of((Integer) objNode.get("id_composant2").asInt());
                Optional<Integer> opt_quantite2 = Optional.of((Integer) objNode.get("quantite2").asInt());

                Recipe recipe = new Recipe(id_article,id_operation,id_composant1,quantite1);
                opt_id_composant2.ifPresent(recipe::setId_composant2);
                opt_quantite2.ifPresent(recipe::setQuantite2);
                recipes.add(recipe);
            }
            return recipes;
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to get all recipes");
        }
    }

    public Recipe getRecipe(int idParameter) throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_RECIPE + idParameter)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            Recipe recipe = null;
            for (final JsonNode objNode : json) {
                int id_article = objNode.get("id_article").asInt();
                int id_operation = objNode.get("id_operation").asInt();
                int id_composant1 = objNode.get("id_composant1").asInt();
                int quantite1 = objNode.get("quantite1").asInt();

                Optional<Integer> opt_id_composant2 = Optional.of((Integer) objNode.get("id_composant2").asInt());
                Optional<Integer> opt_quantite2 = Optional.of((Integer) objNode.get("quantite2").asInt());

                recipe = new Recipe(id_article,id_operation,id_composant1,quantite1);
                opt_id_composant2.ifPresent(recipe::setId_composant2);
                opt_quantite2.ifPresent(recipe::setQuantite2);
            }
            return recipe;
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to find recipe at id : " + idParameter);
        }
    }
}
