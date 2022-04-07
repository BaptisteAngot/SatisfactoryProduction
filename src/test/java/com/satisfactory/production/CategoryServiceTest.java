package com.satisfactory.production;

import com.satisfactory.production.models.Category;
import com.satisfactory.production.services.CategoryService;
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
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void testGetAll() throws IOException {
        List<Category> categories = categoryService.getAllCategories();
        assertThat(categories).isNotEmpty();
    }

    @Test
    public void testGetCategory() throws IOException {
        Category category = categoryService.getCategory(1);
        assertThat(category.getLibelle()).isEqualTo("001");
        assertThat(category.getCode()).isEqualTo("C084");
    }
}
