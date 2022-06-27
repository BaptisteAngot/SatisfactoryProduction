package com.satisfactory.production.services;

import com.satisfactory.production.binaryTree.BinaryTree;
import com.satisfactory.production.binaryTree.Node;
import com.satisfactory.production.models.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BinaryTreeService {
    @Autowired
    private RecipeService recipeService;

    public Node constructNode(Recipe recipe) throws IOException {
        if(recipe == null) {
            return null;
        }
        return new Node(recipe, constructNode(recipeService.getRecipe(recipe.getId_composant1())), constructNode(recipeService.getRecipe(recipe.getId_composant2())));
    }

    public BinaryTree map(Recipe recipe) throws IOException {
        return new BinaryTree(constructNode(recipe));
    }
}
