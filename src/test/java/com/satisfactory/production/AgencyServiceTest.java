package com.satisfactory.production;

import com.satisfactory.production.models.Agency;
import com.satisfactory.production.services.AgencyService;
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
public class AgencyServiceTest {

    @Autowired
    private AgencyService agencyService;

    @Test
    public void testGetAll() throws IOException {
        List<Agency> agencies = agencyService.getAllAgencies();
        assertThat(agencies).isNotEmpty();
    }

    @Test
    public void testGetCategory() throws IOException {
        Agency agency = agencyService.getAgency(1);
        assertThat(agency.getLibelle()).isEqualTo("Alpes-Maritimes");
        assertThat(agency.getCode()).isEqualTo("A0923");
    }

}
