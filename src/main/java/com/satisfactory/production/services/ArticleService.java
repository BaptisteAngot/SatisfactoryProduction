package com.satisfactory.production.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satisfactory.production.models.Article;
import org.jsoup.Connection.Response;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {

    private static final String URLBASE = "http://88.168.248.140:8000/";
    private static final String URL_ARTICLES = "articles/";
    private static final String URL_ARTICLE = "article/";

    public List<Article> getAllArticles() throws IOException {
        Response response = Jsoup.connect(URLBASE + URL_ARTICLES)
                .method(Method.GET)
                .ignoreContentType(true)
                .execute();
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            List<Article> articles = new ArrayList<>();
            for (final JsonNode objNode : json) {
                int id = objNode.get("id").asInt();
                String code = objNode.get("code").asText();
                String libelle = objNode.get("libelle").asText();
                int id_categorie = objNode.get("id_categorie").asInt();
                Article article = new Article(id,code,libelle,id_categorie);
                articles.add(article);
            }
            return articles;
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to get all articles");
        }
    }

    public Article getArticle(int idParameter) throws IOException {
        Response response = Jsoup.connect(URLBASE + URL_ARTICLE + idParameter)
                .method(Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            Article article = null;
            for (final JsonNode objNode : json) {
                int id = objNode.get("id").asInt();
                String code = objNode.get("code").asText();
                String libelle = objNode.get("libelle").asText();
                int id_categorie = objNode.get("id_categorie").asInt();
                article = new Article(id,code,libelle,id_categorie);
            }
            return article;
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to find article at id : " + idParameter);
        }
    }
}
