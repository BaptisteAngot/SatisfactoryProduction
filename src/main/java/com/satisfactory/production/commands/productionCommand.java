package com.satisfactory.production.commands;

import com.satisfactory.production.services.ProductionService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;

@ShellComponent
public class productionCommand {
    @Autowired
    ProductionService productionService;

    @ShellMethod("Launch production from command")
    public String production(@ShellOption int idArticle, int quantity) throws IOException {
        JSONObject response = productionService.launchProduction(idArticle, quantity);
        return "Response: " + response.get("status").toString() + " - Delay : " + response.get("delay").toString();
    }
}
