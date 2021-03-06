package com.satisfactory.production.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satisfactory.production.models.WorkUnit;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkUnitService {
    @Value("${project.api.urlbase}")
    private String URLBASE;
    private static final String URL_WORKUNITS = "workunits/";
    private static final String URL_WORKUNIT = "workunit/";
    private static final String URL_WORKUNITS_BY_OPERATION = "workunit_by_operationid/";

    public List<WorkUnit> getAllWorkUnits() throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_WORKUNITS)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            return parseMultipleWorkUnits(response);
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to get all workunits");
        }
    }

    public WorkUnit getWorkUnit(int idParameter) throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_WORKUNIT + idParameter)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            return parseWorkUnit(response);
        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to find workunit at id : " + idParameter);
        }
    }

    public List<WorkUnit> getWorkUnitsByOperation(int idOperation) throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_WORKUNITS_BY_OPERATION + idOperation)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        if (response.statusCode() == 200) {
            return parseMultipleWorkUnits(response);

        }else {
            throw new IOException("HTTP code " + response.statusCode() + " to get workunits for operation " + idOperation);
        }
    }

    private List<WorkUnit> parseMultipleWorkUnits(Connection.Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String jsonText = response.body();
        JsonNode json = mapper.readTree(jsonText);
        List<WorkUnit> workUnits = new ArrayList<>();
        for (final JsonNode objNode : json) {
            int id = objNode.get("id").asInt();
            String code = objNode.get("code").asText();
            String libelle = objNode.get("libelle").asText();
            WorkUnit workUnit = new WorkUnit(id,code, libelle);
            workUnits.add(workUnit);
        }
        return workUnits;
    }

    private WorkUnit parseWorkUnit(Connection.Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String jsonText = response.body();
        JsonNode json = mapper.readTree(jsonText);
        WorkUnit workUnit = null;
        for (final JsonNode objNode : json) {
            int id = objNode.get("id").asInt();
            String code = objNode.get("code").asText();
            String libelle = objNode.get("libelle").asText();
            workUnit = new WorkUnit(id,code, libelle);
        }
        return workUnit;
    }
}
