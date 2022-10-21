package com.expensetracker.web.thymeleaf;

import com.expensetracker.model.Category;
import com.expensetracker.service.HttpService;
import com.expensetracker.web.dto.CategoryDto;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/web/categories")
public class CategoryWebController {

  private final HttpService<CategoryDto> httpService;

  private final String CATEGORY_URI;

  public CategoryWebController(ServerProperties serverProperties, HttpService<CategoryDto> httpService) {
    this.CATEGORY_URI = "http://localhost:" + serverProperties.getPort() + "/categories";
    this.httpService = httpService;
  }

  @GetMapping
  public String getAllCategories(Model model) {
    model.addAttribute("categories", httpService.get(CATEGORY_URI, List.class));
    return "categories/index";
  }

  @GetMapping("/add")
  public String createCategoryForm(Model model) {
    Category newCategory = new Category();
    model.addAttribute("newCategory", newCategory);
    return "categories/create";
  }

  @PostMapping
  public String createCategory(@ModelAttribute("category") CategoryDto category) {
    httpService.post(category, CATEGORY_URI);
    return "redirect:/web/categories";
  }

  @GetMapping("/edit/{id}")
  public String updateCategoryForm(@PathVariable Integer id, Model model) {
    CategoryDto categoryDto = httpService.get(CATEGORY_URI + "/" + id, CategoryDto.class);
    model.addAttribute("category", categoryDto);
    return "categories/edit";
  }

  @GetMapping("/{id}")
  public String updateCategory(@PathVariable Integer id, @ModelAttribute("category") CategoryDto category) {
    CategoryDto categoryDto = new CategoryDto(null, category.getName());
    httpService.put(categoryDto, CATEGORY_URI + "/" + id);
    return "redirect:/web/categories";
  }

  @GetMapping("/delete/{id}")
  public String deleteCategory(@PathVariable Integer id) {
    httpService.delete(CATEGORY_URI + "/" + id);
    return "redirect:/web/categories";
  }

}
