package com.satisfactory.production.services;

import com.satisfactory.production.binaryTree.BinaryTree;
import com.satisfactory.production.binaryTree.Node;
import com.satisfactory.production.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class BinaryTreeService {
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private WorkUnitService workUnitService;

    @Autowired
    private OperationService operationService;

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
        Map<WorkUnit, Integer> workUnits = new HashMap<>();
        List<Order> orderList = new ArrayList<>();
        List<Article> articles = articleService.getAllArticles();
        List<Operation> operationList = operationService.getAllOperations();
        LinkedHashMap<Recipe, TupleOrder> articleQty = new LinkedHashMap<>();
        parcoursPostfixe(node, qty, articleQty);
        LinkedHashMap<Recipe, TupleOrder> articleQtySorted =  articleQty.entrySet()
                .stream()
                .sorted(Comparator.comparing(tupleOrderIntegerEntry -> tupleOrderIntegerEntry.getValue().getDeep()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        // create order here
        for (Map.Entry<Recipe, TupleOrder> entryArticleQty : articleQtySorted.entrySet()) {
            boolean pushed = false;
            Optional<Article> optArticle = articles.stream()
                    .filter(a -> a.getId() == entryArticleQty.getKey().getId_article())
                    .findFirst();

            Optional<Operation> optOperation = operationList.stream()
                    .filter(operation -> operation.getId() == entryArticleQty.getKey().getId_operation())
                    .findFirst();

            int delais = 0;
            if(optOperation.isPresent()) {
                Operation operation = optOperation.get();
                delais = operation.getDelaiInstallation();
                delais += operation.getDelai() * entryArticleQty.getValue().getQty();
            }

            // article1 = article/2

            // int div = entryArticleQty.getValue().getQty() / 100;

            List<Map.Entry<WorkUnit,Set<Integer>>> workUnitListFiltered = listWorkUnits
                    .entrySet()
                    .stream()
                    .filter(workUnitSetEntry -> {
                        // take only workUnit with the same operation
                        return workUnitSetEntry.getValue().contains(entryArticleQty.getKey().getId_operation());
                    })
                    .collect(Collectors.toList());



            int loop = 0;


            WorkUnit workUnitSelected = null;
            int nbOperation = 99999999;
            for (Map.Entry<WorkUnit, Set<Integer>> entryWorkUnit : workUnitListFiltered){
                if (orderList.size() == 0){
                    workUnitSelected = entryWorkUnit.getKey();
                    break;
                }else {
                    for (Order order: orderList) {
                        if (order.getCodeWorkUnit().equals(entryWorkUnit.getKey().getCode())){
                            if(order.getProductOperations().size() < nbOperation){
                                nbOperation = order.getProductOperations().size();
                                workUnitSelected = entryWorkUnit.getKey();
                            }
                        }else{
                            workUnitSelected = entryWorkUnit.getKey();
                            break;
                        }
                    }
                }
            }
            do {
                WorkUnit finalWorkUnitSelected = workUnitSelected;
                Optional<Order> optOrder = orderList.stream()
                        .filter(order -> {
                            assert finalWorkUnitSelected != null;
                            return finalWorkUnitSelected.getCode().equals(order.getCodeWorkUnit());
                        })
                        .findFirst();

                Optional<OperationOrder> optDeep = null;
                if (optOrder.isPresent()){
                    optDeep = optOrder.get().getProductOperations().stream()
                            .filter(tupleOrder -> tupleOrder.getOrder() == entryArticleQty.getValue().getDeep())
                            .findFirst();
                }

                    /*Integer delaiTotalWorkunit = workUnits.getOrDefault(entryWorkUnit.getKey(), 0);

                    int moyenne = 0;
                    for (Map.Entry<WorkUnit, Integer> workUnitIntegerMap: workUnits.entrySet()) {
                        moyenne = workUnitIntegerMap.getValue() + moyenne;
                    }
                    if (workUnits.size() != 0) {
                        moyenne = moyenne / workUnits.size();
                    }*/

                if ((!optOrder.isPresent() || !optDeep.isPresent())) {

                    if (optArticle.isPresent()) {
                        Article article = optArticle.get();
                        /*int res =
                                div == 0 ?
                                        entryArticleQty.getValue().getQty() :
                                        div+1 == loop ?
                                                (entryArticleQty.getValue().getQty() % div) + (entryArticleQty.getValue().getQty() / div) :
                                                entryArticleQty.getValue().getQty() / div;*/

                        if (optOrder.isPresent()){
                            workUnits.put(finalWorkUnitSelected, workUnits.get(finalWorkUnitSelected) + delais);
                            OperationOrder operationOrder = new OperationOrder(article.getCode(), entryArticleQty.getValue().getQty(), entryArticleQty.getValue().getDeep());
                            optOrder.get().getProductOperations().add(operationOrder);
                        }else{
                            Order order = new Order();
                            workUnits.put(finalWorkUnitSelected, delais);
                            assert finalWorkUnitSelected != null;
                            order.setCodeWorkUnit(finalWorkUnitSelected.getCode());
                            List<OperationOrder> operationOrderList = new ArrayList<>();
                            OperationOrder operationOrder = new OperationOrder(article.getCode(), entryArticleQty.getValue().getQty(), entryArticleQty.getValue().getDeep());
                            operationOrderList.add(operationOrder);
                            order.setProductOperations(operationOrderList);
                            orderList.add(order);
                        }
                    }
                    break;
                }
                loop++;
            }while (false);

        }

        List<Order> oderListToReturn = new ArrayList<>();
        orderList.forEach(order -> {
            List<OperationOrder> operationOrderSorted = order.getProductOperations().stream().sorted(Comparator.comparing(OperationOrder::getOrder)).collect(Collectors.toList());
           for (int i=0; i < operationOrderSorted.size(); i++) {
               operationOrderSorted.get(i).setOrder(i+1);
           }
            order.setProductOperations(operationOrderSorted);
            oderListToReturn.add(order);
        });

        return oderListToReturn;
    }

    private int parcoursPostfixe(Node node, int qty, LinkedHashMap<Recipe, TupleOrder> articleQty) {
        int y = 1;
        int x = 1;
        if (node.getLeft() != null) {
            y = parcoursPostfixe(node.getLeft(), node.getRecipe().getQuantite1()*qty, articleQty);
        }
        if (node.getRight() != null) {
            x = parcoursPostfixe(node.getRight(), node.getRecipe().getQuantite2()*qty, articleQty);
        }
        int i = Math.max(y, x);

        if (articleQty.containsKey(node.getRecipe())){
            TupleOrder tupleOrder = articleQty.get(node.getRecipe());
            tupleOrder.setQty(tupleOrder.getQty()+qty);
            articleQty.put(
                    node.getRecipe(),
                    tupleOrder);
        }else{
            TupleOrder tupleOrder = new TupleOrder(i,qty);
            articleQty.put(node.getRecipe(), tupleOrder);
        }
        return i+1;
    }
}
