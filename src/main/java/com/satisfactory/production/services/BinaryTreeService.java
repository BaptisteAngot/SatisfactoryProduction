package com.satisfactory.production.services;

import com.satisfactory.production.binaryTree.BinaryTree;
import com.satisfactory.production.binaryTree.Node;
import com.satisfactory.production.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BinaryTreeService {
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private WorkUnitService workUnitService;

    public Node constructNode(Recipe recipe) throws IOException {
        if(recipe == null) {
            return null;
        }
        return new Node(recipe, constructNode(recipeService.getRecipe(recipe.getId_composant1())), constructNode(recipeService.getRecipe(recipe.getId_composant2())));
    }

    public BinaryTree map(Recipe recipe) throws IOException {
        return new BinaryTree(constructNode(recipe));
    }

    public List<Order> getOrderList(Node node, int qty) throws IOException {
        List<WorkUnit> workUnits = new ArrayList<>();
        List<Order> orderList = new ArrayList<>();
        parcoursPostfixe(node, qty, workUnits, orderList);
        return orderList;
    }

    private void parcoursPostfixe(Node node, int qty, List<WorkUnit> workUnits, List<Order> orderList) throws IOException {
        if (node.getLeft() != null) {
            parcoursPostfixe(node.getLeft(), node.getRecipe().getQuantite1() ,workUnits, orderList);
        }
        if (node.getRight() != null) {
            parcoursPostfixe(node.getRight(), node.getRecipe().getQuantite2() ,workUnits, orderList);
        }

        List<WorkUnit> workUnitAvaiblable = workUnitService.getWorkUnitsByOperation(node.getRecipe().getId_operation());
        for(WorkUnit workUnit : workUnitAvaiblable) {
            if(!workUnits.contains(workUnit)) {
                workUnits.add(workUnit);

                // Création de l'ordre de commande
                Order order = new Order();
                order.setCodeWorkUnit(workUnit.getCode());
                Article article = articleService.getArticle(node.getRecipe().getId_article());
                List<OperationOrder> operationOrderList = new ArrayList<>();
                OperationOrder operationOrder = new OperationOrder(article.getCode(),qty, 1);
                operationOrderList.add(operationOrder);
                order.setProductOperations(operationOrderList);
                orderList.add(order);
                break;
            }
        }
        System.out.println(node.getRecipe().getId_article());
    }
}
