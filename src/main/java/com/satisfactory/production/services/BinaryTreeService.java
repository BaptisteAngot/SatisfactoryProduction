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
        LinkedHashMap<TupleOrder, Integer> articleQty = new LinkedHashMap<>();
        parcoursPostfixe(node, qty, articleQty);
        LinkedHashMap<TupleOrder, Integer> articleQtySorted =  articleQty.entrySet()
                .stream()
                .sorted(Comparator.comparing(tupleOrderIntegerEntry -> tupleOrderIntegerEntry.getKey().getDeep()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        // create order here
        for (Map.Entry<TupleOrder, Integer> entryArticleQty : articleQtySorted.entrySet()) {
            boolean pushed = false;
            Optional<Article> optArticle = articles.stream()
                    .filter(a -> a.getId() == entryArticleQty.getKey().getRecipe().getId_article())
                    .findFirst();
            Optional<Operation> optOperation = operationList.stream()
                    .filter(operation -> operation.getId() == entryArticleQty.getKey().getRecipe().getId_operation())
                    .findFirst();

            int delais = 0;
            if(optOperation.isPresent()) {
                Operation operation = optOperation.get();
                delais = operation.getDelaiInstallation();
                delais += operation.getDelai() * entryArticleQty.getValue();
            }

            // article1 = article/2

            // 4 = 200/50
            int div = entryArticleQty.getValue() / 100;

            int loop = 1;
            do {
                for (Map.Entry<WorkUnit, Set<Integer>> entryWorkUnit : listWorkUnits.entrySet()) {

                    Optional<Order> optOrder = orderList.stream()
                            .filter(order -> entryWorkUnit.getKey().getCode().equals(order.getCodeWorkUnit()))
                            .findFirst();
                    Optional<OperationOrder> optDeep = null;
                    if (optOrder.isPresent()){
                        optDeep = optOrder.get().getProductOperations().stream()
                                .filter(tupleOrder -> tupleOrder.getOrder() == entryArticleQty.getKey().getDeep())
                                .findFirst();
                    }

                    Integer delaiTotalWorkunit = workUnits.getOrDefault(entryWorkUnit.getKey(), 0);

                    int moyenne = 0;
                    for (Map.Entry<WorkUnit, Integer> workUnitIntegerMap: workUnits.entrySet()) {
                        moyenne = workUnitIntegerMap.getValue() + moyenne;
                    }
                    if (workUnits.size() != 0) {
                        moyenne = moyenne / workUnits.size();
                    }

                    if (entryWorkUnit.getValue().contains(entryArticleQty.getKey().getRecipe().getId_operation()) &&
                            (!optOrder.isPresent() || !optDeep.isPresent()) &&
                            (moyenne == 0 || moyenne*loop > delaiTotalWorkunit+delais)
                    ) {

                        if (optArticle.isPresent()) {
                            Article article = optArticle.get();
                            int res =
                                    div == 0 ?
                                    entryArticleQty.getValue() :
                                    div+1 == loop ?
                                            (entryArticleQty.getValue() % div) + (entryArticleQty.getValue() / div) :
                                            entryArticleQty.getValue() / div;

                            if (optOrder.isPresent()){
                                workUnits.put(entryWorkUnit.getKey(), workUnits.get(entryWorkUnit.getKey()) + delais);
                                OperationOrder operationOrder = new OperationOrder(article.getCode(), res, entryArticleQty.getKey().getDeep());
                                optOrder.get().getProductOperations().add(operationOrder);
                            }else{
                                Order order = new Order();
                                workUnits.put(entryWorkUnit.getKey(), delais);
                                order.setCodeWorkUnit(entryWorkUnit.getKey().getCode());
                                List<OperationOrder> operationOrderList = new ArrayList<>();
                                OperationOrder operationOrder = new OperationOrder(article.getCode(), res, entryArticleQty.getKey().getDeep());
                                operationOrderList.add(operationOrder);
                                order.setProductOperations(operationOrderList);
                                orderList.add(order);
                            }
                        }
                        pushed = true;
                        break;
                    }
                }
                loop++;
            }while (!pushed && loop != div+1);

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

    private int parcoursPostfixe(Node node, int qty, LinkedHashMap<TupleOrder, Integer> articleQty) {
        int y = 1;
        int x = 1;
        if (node.getLeft() != null) {
            y = parcoursPostfixe(node.getLeft(), node.getRecipe().getQuantite1(), articleQty);
        }
        if (node.getRight() != null) {
            x = parcoursPostfixe(node.getRight(), node.getRecipe().getQuantite2(), articleQty);
        }
        int i = Math.max(y, x);
        TupleOrder tupleOrder = new TupleOrder(i, node.getRecipe());
        if (articleQty.containsKey(tupleOrder)){
            articleQty.put(tupleOrder,articleQty.get(tupleOrder) + qty);
        }else{
            articleQty.put(tupleOrder, qty);
        }
        return i+1;
    }
}
