package com.satisfactory.production;

import com.satisfactory.production.models.WorkUnit;
import com.satisfactory.production.services.WorkUnitService;
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
public class WorkUnitsServiceTest {

    @Autowired
    private WorkUnitService workUnitService;


    @Test
    public void testGetAll() throws IOException {
        List<WorkUnit> workUnits = workUnitService.getAllWorkUnits();
        assertThat(workUnits).isNotEmpty();
    }

    @Test
    public void testGetWorkUnit() throws IOException {
        WorkUnit workUnit = workUnitService.getWorkUnit(1);
        assertThat(workUnit.getLibelle()).isEqualTo("241395061");
        assertThat(workUnit.getCode()).isEqualTo("W055");
    }

    @Test
    public void testgetWorkUnitsByOperation() throws IOException {
        List<WorkUnit> workUnits = workUnitService.getWorkUnitsByOperation(2);
        assertThat(workUnits).isNotEmpty();
    }
}
