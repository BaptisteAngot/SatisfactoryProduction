package com.satisfactory.production.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satisfactory.production.models.Category;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private static final String URLBASE = "http://88.168.248.140:8000/";
    private static final String URL_CATEGORIES = "categories/";
    private static final String URL_CATEGORY = "category/";

    public List<Category> getAllCategories() throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_CATEGORIES)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            List<Category> categories = new ArrayList<>();
            for (final JsonNode objNode : json) {
                int id = objNode.get("id").asInt();
                String code = objNode.get("code").asText();
                String libelle = objNode.get("libelle").asText();
                Category category = new Category(id,code,libelle);
                categories.add(category);
            }
            return categories;
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to get all categories");
        }
    }

    public Category getCategory(int idParameter) throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_CATEGORY + idParameter)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            Category category = null;
            for (final JsonNode objNode : json) {
                int id = objNode.get("id").asInt();
                String code = objNode.get("code").asText();
                String libelle = objNode.get("libelle").asText();
                category = new Category(id,code,libelle);
            }
            return category;
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to find category at id : " + idParameter);
        }
    }

}
