package com.satisfactory.production.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satisfactory.production.models.Operation;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OperationService {
    private static final String URLBASE = "http://88.168.248.140:8000/";
    private static final String URL_OPERATIONS = "operations/";
    private static final String URL_OPERATION = "operation/";

    public List<Operation> getAllOperations() throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_OPERATIONS)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            List<Operation> Operations = new ArrayList<>();
            for (final JsonNode objNode : json) {
                int id = objNode.get("id").asInt();
                String code = objNode.get("code").asText();
                String libelle = objNode.get("libelle").asText();
                int delai = objNode.get("delai").asInt();
                int delaiInstallation = objNode.get("delaiInstallation").asInt();
                Operation operation = new Operation(id,code,libelle,delai,delaiInstallation);
                Operations.add(operation);
            }
            return Operations;
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
    
}
