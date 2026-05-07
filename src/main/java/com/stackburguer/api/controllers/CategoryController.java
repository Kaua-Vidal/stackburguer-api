package com.stackburguer.api.controllers;

import com.stackburguer.api.DTO.category.CategoryRequestDTO;
import com.stackburguer.api.DTO.category.CategoryResponseDTO;
import com.stackburguer.api.models.Category;
import com.stackburguer.api.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAll(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO requestDto){

        CategoryResponseDTO response = categoryService.createCategory(requestDto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(
            @PathVariable String id,
            @RequestBody CategoryRequestDTO dto){
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @PostMapping("/uploads/{id}")
    public ResponseEntity<CategoryResponseDTO> uploadImage(@PathVariable String id, @RequestParam("file")MultipartFile file){
        Category updatedCategory = categoryService.uploadImage(id, file);
        CategoryResponseDTO response = categoryService.mapToResponseDTO(updatedCategory);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }



}
