package com.satisfactory.production.services;

import com.satisfactory.production.binaryTree.BinaryTree;
import com.satisfactory.production.binaryTree.Node;
import com.satisfactory.production.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class BinaryTreeService {
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private WorkUnitService workUnitService;

    public Node constructNode(Recipe recipe, List<Recipe> allRecipes, Map<WorkUnit, Set<Integer>> listWorkUnits) throws IOException {
        if (recipe == null) {
            return null;
        }
        Optional<Recipe> optRecipe1 = allRecipes.stream()
                .filter(r -> r.getId_article() == recipe.getId_composant1())
                .findFirst();
        Optional<Recipe> optRecipe2 = allRecipes.stream()
                .filter(r -> r.getId_article() == recipe.getId_composant2())
                .findFirst();
        Recipe recipe1 = optRecipe1.orElse(null);
        Recipe recipe2 = optRecipe2.orElse(null);

        List<WorkUnit> workUnitAvaiblable = new ArrayList<>();
        for (Map.Entry<WorkUnit, Set<Integer>> entry : listWorkUnits.entrySet()) {
            if (entry.getValue().contains(recipe.getId_operation())) {
                workUnitAvaiblable.add(entry.getKey());
            }
        }

        if (workUnitAvaiblable.size() == 0) {
            workUnitAvaiblable = workUnitService.getWorkUnitsByOperation(recipe.getId_operation());
        }
        for (WorkUnit workUnit : workUnitAvaiblable) {
            if (listWorkUnits.containsKey(workUnit)) {
                listWorkUnits.get(workUnit).add(recipe.getId_operation());
            } else {
                listWorkUnits.put(workUnit, new HashSet<Integer>(recipe.getId_operation()));
            }
        }
        return new Node(recipe, constructNode(recipe1, allRecipes, listWorkUnits), constructNode(recipe2, allRecipes, listWorkUnits));
    }

    public Tuple map(Recipe recipe) throws IOException {
        List<Recipe> listAllRecipe = recipeService.getAllService();
        Map<WorkUnit, Set<Integer>> listWorkUnits = new HashMap<>();
        return new Tuple(new BinaryTree(constructNode(recipe, listAllRecipe, listWorkUnits)), listWorkUnits);
    }

    public List<Order> getOrderList(Node node, int qty, Map<WorkUnit, Set<Integer>> listWorkUnits) throws IOException {
        List<WorkUnit> workUnits = new ArrayList<>();
        List<Order> orderList = new ArrayList<>();
        List<Article> articles = articleService.getAllArticles();
        LinkedHashMap<Recipe, Integer> articleQty = new LinkedHashMap<>();
        parcoursPostfixe(node, qty, workUnits, orderList, articles, listWorkUnits,articleQty);

        // create order here
        for (Map.Entry<Recipe, Integer> entryArticleQty : articleQty.entrySet()) {
            for (Map.Entry<WorkUnit, Set<Integer>> entryWorkUnit : listWorkUnits.entrySet()) {
                if (entryWorkUnit.getValue().contains(entryArticleQty.getKey().getId_operation()) && !workUnits.contains(entryWorkUnit.getKey())) {
                    workUnits.add(entryWorkUnit.getKey());
                    Order order = new Order();
                    order.setCodeWorkUnit(entryWorkUnit.getKey().getCode());
                    Optional<Article> optArticle = articles.stream()
                            .filter(a -> a.getId() == entryArticleQty.getKey().getId_article())
                            .findFirst();
                    if (optArticle.isPresent()) {
                        Article article = optArticle.get();
                        List<OperationOrder> operationOrderList = new ArrayList<>();
                        OperationOrder operationOrder = new OperationOrder(article.getCode(),entryArticleQty.getValue(), 1);
                        operationOrderList.add(operationOrder);
                        order.setProductOperations(operationOrderList);
                        orderList.add(order);
                    }
                    break;
                }
            }
        }
        return orderList;
    }

    private void parcoursPostfixe(Node node, int qty, List<WorkUnit> workUnits, List<Order> orderList, List<Article> articles, Map<WorkUnit, Set<Integer>> listWorkUnits, LinkedHashMap<Recipe, Integer> articleQty) throws IOException {
        if (node.getLeft() != null) {
            parcoursPostfixe(node.getLeft(), node.getRecipe().getQuantite1(), workUnits, orderList, articles, listWorkUnits, articleQty);
        }
        if (node.getRight() != null) {
            parcoursPostfixe(node.getRight(), node.getRecipe().getQuantite2(), workUnits, orderList, articles, listWorkUnits, articleQty);
        }
        articleQty.put(node.getRecipe(), articleQty.getOrDefault(node.getRecipe(), 0) + qty);

    }
}
