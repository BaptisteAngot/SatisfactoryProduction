package com.satisfactory.production.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satisfactory.production.models.Agency;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgencyService {
    private static final String URLBASE = "http://88.168.248.140:8000/";
    private static final String URL_AGENCIES = "agencies/";
    private static final String URL_AGENCY = "agency/";

    public List<Agency> getAllAgencies() throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_AGENCIES)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            List<Agency> agencies = new ArrayList<>();
            for (final JsonNode objNode : json) {
                int id = objNode.get("id").asInt();
                String code = objNode.get("code").asText();
                String libelle = objNode.get("libelle").asText();
                Agency agency = new Agency(id, code, libelle);
                agencies.add(agency);
            }
            return agencies;
        } else {
            throw new IOException("HTTP code " + response.statusCode() + " to get all agencies");
        }
    }

    public Agency getAgency(int idParameter) throws IOException {
        Connection.Response response = Jsoup.connect(URLBASE + URL_AGENCY + idParameter)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute();

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            String jsonText = response.body();
            JsonNode json = mapper.readTree(jsonText);
            Agency agency = null;
            for (final JsonNode objNode : json) {
                int id = objNode.get("id").asInt();
                String code = objNode.get("code").asText();
                String libelle = objNode.get("libelle").asText();
                agency = new Agency(id, code, libelle);
            }
            return agency;
        } else {
            throw new IOException("HTTP code " + response.statusCode() + " to find agency at id : " + idParameter);
        }
    }
}
