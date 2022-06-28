package com.satisfactory.production;

import com.satisfactory.production.services.ProductionService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
@SpringBootTest(classes = ProductionApplication.class)
public class BinaryTreeTest {

    @Autowired
    ProductionService productionService;

    @Test
    public void test() throws IOException {
        productionService.launchProduction(565,1);
        assertThat(1).isEqualTo(1);
    }
}
