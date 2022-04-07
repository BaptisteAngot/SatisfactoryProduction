package com.satisfactory.production;

import com.satisfactory.production.models.Operation;
import com.satisfactory.production.services.OperationService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
@SpringBootTest(classes = ProductionApplication.class)
public class OperationServiceTest {

    @Autowired
    private OperationService operationService;

    @Test
    public void testGetAll() throws IOException {
        List<Operation> operations = operationService.getAllOperations();
        assertThat(operations).isNotEmpty();
    }

    @Test
    public void testGetOperation() throws IOException {
        Operation operation = operationService.getOperation(1);
        assertThat(operation.getLibelle()).isEqualTo("Perçage");
        assertThat(operation.getCode()).isEqualTo("O09");
        assertThat(operation.getDelai()).isEqualTo(2);
        assertThat(operation.getDelaiInstallation()).isEqualTo(0);
    }
}
