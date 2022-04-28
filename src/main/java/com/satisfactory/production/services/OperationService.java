package com.satisfactory.production.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satisfactory.production.models.Operation;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperationService {
    @Value("${project.api.urlbase}")
    private String URLBASE;
    private static final String URL_OPERATIONS = "operations/";
    private static final String URL_OPERATION = "operation/";
    private static final String URL_OPERATIONS_BY_WORKUNIT = "operations_by_workunit/";


    public List<Operation> getAllOperations() throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_OPERATIONS)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            List<Operation> operations = new ArrayList<>();
            for (final JsonNode objNode : json) {
                int id = objNode.get("id").asInt();
                String code = objNode.get("code").asText();
                String libelle = objNode.get("libelle").asText();
                int delai = objNode.get("delai").asInt();
                int delaiInstallation = objNode.get("delaiInstallation").asInt();
                Operation operation = new Operation(id,code,libelle,delai,delaiInstallation);
                operations.add(operation);
            }
            return operations;
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to get all Operations");
        }
    }

    public Operation getOperation(int idParameter) throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_OPERATION + idParameter)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            Operation operation = null;
            for (final JsonNode objNode : json) {
                int id = objNode.get("id").asInt();
                String code = objNode.get("code").asText();
                String libelle = objNode.get("libelle").asText();
                int delai = objNode.get("delai").asInt();
                int delaiInstallation = objNode.get("delaiInstallation").asInt();

                operation = new Operation(id,code,libelle,delai,delaiInstallation);
            }
            return operation;
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to find Operation at id : " + idParameter);
        }
    }

    public List<Operation> getOperationsByWorkUnit(int idWorkUnit) throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_OPERATIONS_BY_WORKUNIT + idWorkUnit)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            List<Integer> idOperations = new ArrayList<>();
            List<Operation> allOperations = getAllOperations();
            for (final JsonNode objNode : json) {
                int idOperation = objNode.get("id").asInt();
                idOperations.add(idOperation);
            }

            return allOperations.stream()
                    .filter(operation -> idOperations.contains(operation.getId()))
                    .collect(Collectors.toList());
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to find OperationByWorkUnit at id : " + idWorkUnit);
        }
    }
    
}
