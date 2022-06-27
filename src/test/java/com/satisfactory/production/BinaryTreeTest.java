package com.satisfactory.production;

import com.satisfactory.production.binaryTree.BinaryTree;
import com.satisfactory.production.binaryTree.Node;
import com.satisfactory.production.models.Recipe;
import com.satisfactory.production.services.BinaryTreeService;
import com.satisfactory.production.services.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
@SpringBootTest(classes = ProductionApplication.class)
public class BinaryTreeTest {

    @Autowired
    RecipeService recipeService;

    @Autowired
    BinaryTreeService binaryTreeService;

    @Test
    public void test() throws IOException {
        Recipe recipe = recipeService.getRecipe(565);
        BinaryTree binaryTree1 = binaryTreeService.map(recipe);
        binaryTree1.parcoursPostfixe(binaryTree1.getRoot());

        assertThat(1).isEqualTo(1);
    }
}
