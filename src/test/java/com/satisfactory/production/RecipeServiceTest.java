package com.satisfactory.production;

import com.satisfactory.production.models.Recipe;
import com.satisfactory.production.services.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
@SpringBootTest(classes = ProductionApplication.class)
public class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;

    @Test
    public void testGetAll() throws IOException {
        List<Recipe> recipes = recipeService.getAllService();
        assertThat(recipes).isNotEmpty();
    }

    @Test
    public void testGetRecipe() throws IOException {
        Recipe recipe = recipeService.getRecipe(1);
        assertThat(recipe.getId_article()).isEqualTo(1);
        assertThat(recipe.getId_operation()).isEqualTo(7);
        assertThat(recipe.getId_composant1()).isEqualTo(166);
        assertThat(recipe.getQuantite1()).isEqualTo(9);
        assertThat(recipe.getId_composant2()).isNull();
        assertThat(recipe.getQuantite2()).isNull();
    }
}
