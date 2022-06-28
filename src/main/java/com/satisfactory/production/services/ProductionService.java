package com.satisfactory.production.services;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satisfactory.production.models.Order;
import com.satisfactory.production.models.Recipe;
import com.satisfactory.production.models.Tuple;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProductionService {

    @Value("${project.api.urlbase}")
    private String URLBASE;

    private static final String URL_PRODUCTION = "production";
    @Autowired
    RecipeService recipeService;

    @Autowired
    BinaryTreeService binaryTreeService;

    public JSONObject launchProduction(int idArticle, int qty) throws IOException {
        Recipe recipe = recipeService.getRecipe(idArticle);
        Tuple tuple = binaryTreeService.map(recipe);

        List<Order> orderList = binaryTreeService.getOrderList(tuple.getTree().getRoot(), qty, tuple.getListWorkUnits());
        Connection.Response test = makeRequest(orderList);
        return new JSONObject(test.body());
    }

    private Connection.Response makeRequest(List<Order> orderList) throws IOException {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(orderList);
        json = json.replace("\r\n","");
        Connection.Response response = Jsoup.connect(URLBASE+ URL_PRODUCTION)
                .method(Connection.Method.POST)
                .header("Content-Type", "application/json;charset=UTF-8")
                .ignoreContentType(true)
                .requestBody(json)
                .execute();

        return response;
    }
}
