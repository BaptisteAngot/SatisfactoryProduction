package com.satisfactory.production;

import com.satisfactory.production.models.Article;
import com.satisfactory.production.services.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

@RunWith(JUnit4.class)
@SpringBootTest(classes = ProductionApplication.class)
public class ArticleServiceTest {
    @Autowired
    private ArticleService articleService;

    @Test
    public void testGetAll() throws IOException {
        List<Article> articles = articleService.getAllArticles();
        assertThat(articles).isNotEmpty();
    }

    @Test
    public void testGetArticle() throws IOException {
        Article article = articleService.getArticle(1);
        assertThat(article.getLibelle()).isEqualTo("Lorem");
        assertThat(article.getCode()).isEqualTo("P915874199");
        assertThat(article.getId_categorie()).isEqualTo(1);
    }
}
