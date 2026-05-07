package com.stackburguer.api.service;

import com.stackburguer.api.DTO.category.CategoryRequestDTO;
import com.stackburguer.api.DTO.category.CategoryResponseDTO;
import com.stackburguer.api.exceptions.CategoryNotFoundException;
import com.stackburguer.api.models.Category;
import com.stackburguer.api.repositories.CategoryRepository;
import com.stackburguer.api.utils.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private S3Util s3Util;

    @Value("${url.s3.category}")
    private String urlS3Category;

    public CategoryResponseDTO mapToResponseDTO(Category category){
        String path = category.getPath();

        String url = (path != null && path.startsWith("http"))
                ? path
                : urlS3Category + path;

        return new CategoryResponseDTO(category.getId().toString(), category.getName(), url, path);
    }

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryDTO){
        Category category = new Category();

        category.setName(categoryDTO.name());

        return mapToResponseDTO(categoryRepository.save(category)) ;
    }

    public Category uploadImage(String id, MultipartFile file){
        UUID categoryId = UUID.fromString(id);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Categoria não existe."));

        String fileName = s3Util.uploadFile(file);

        category.setPath(fileName);
        return categoryRepository.save(category);
    }

    public CategoryResponseDTO update(String id, CategoryRequestDTO dto){
        UUID categoryId = UUID.fromString(id);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Categoria não encontrada."));
        category.setName(dto.name());
        return mapToResponseDTO(categoryRepository.save(category));
    }

    public void delete(String id) {
        UUID categoryId = UUID.fromString(id);

        if (!categoryRepository.existsById(categoryId)){
            throw new CategoryNotFoundException("Categoria não encontrada.");
        }
        categoryRepository.deleteById(categoryId);
    }
}
