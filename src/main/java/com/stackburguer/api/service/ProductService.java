package com.stackburguer.api.service;

import com.stackburguer.api.DTO.product.ProductRequestDTO;
import com.stackburguer.api.DTO.product.ProductResponseDTO;
import com.stackburguer.api.exceptions.CategoryNotFoundException;
import com.stackburguer.api.exceptions.ProductNotFoundException;
import com.stackburguer.api.models.Category;
import com.stackburguer.api.models.Product;
import com.stackburguer.api.repositories.ProductRepository;
import com.stackburguer.api.repositories.CategoryRepository;
import com.stackburguer.api.utils.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final S3Util s3Util;

    public ProductService(S3Util s3Util){
        this.s3Util = s3Util;
    }

    public List<ProductResponseDTO> getAllProducts(){
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // 🚀 MÉTODO createProduct ATUALIZADO PARA O S3
    public ProductResponseDTO createProduct(String productJson, MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequestDTO requestDto = objectMapper.readValue(productJson, ProductRequestDTO.class);

        if(requestDto.price() <= 0){
            throw new RuntimeException("O preço do produto deve ser maior que zero.");
        }

        Category category = categoryRepository.findById(requestDto.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Categoria não encontrada!"));

        Product product = new Product();
        product.setName(requestDto.name());
        product.setPrice(requestDto.price());
        product.setCategory(category);
        product.setOffer(requestDto.offer() != null ? requestDto.offer() : false);

        // A MÁGICA ACONTECE AQUI: Substituímos todo o código local por 1 linha do S3
        String fileName = s3Util.uploadFile(file);
        product.setPath(fileName);

        Product savedProduct = productRepository.save(product);
        return mapToResponseDTO(savedProduct);
    }

    private ProductResponseDTO mapToResponseDTO(Product product){
        return new ProductResponseDTO(product);
    }

    public List<ProductResponseDTO> getProductsByCategory(UUID categoryId){
        List<Product> products = productRepository.findByCategoryId(categoryId.toString());

        if(!categoryRepository.existsById(categoryId)){
            throw new CategoryNotFoundException("Categoria ID " + categoryId + " não encontrada.");
        }

        return products.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // 🚀 MÉTODO deleteProduct LIMPO
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));

        // Removida a lógica de deletar arquivo local (não precisamos mais nos preocupar com o HD do Render)
        // Opcional no futuro: Criar um s3Util.deleteFile(product.getPath()) para apagar da Amazon também.

        productRepository.deleteById(id);
    }

    // 🚀 MÉTODO updateProduct ATUALIZADO PARA O S3
    public ProductResponseDTO updateProduct(Long id, String productJson, MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequestDTO dto = objectMapper.readValue(productJson, ProductRequestDTO.class);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("A categoria informada não existe no banco"));

        existingProduct.setName(dto.name());
        existingProduct.setPrice(dto.price());
        existingProduct.setCategory(category);
        existingProduct.setOffer(dto.offer());

        if(file != null && !file.isEmpty()) {
            // Substituímos o salvamento local pela chamada limpa do S3
            String fileName = s3Util.uploadFile(file);
            existingProduct.setPath(fileName);
        }

        Product updatedProduct = productRepository.save(existingProduct);

        return mapToResponseDTO(updatedProduct);
    }

    public String updateProductImage(Long productId, MultipartFile file){
        String fileUrl = s3Util.uploadFile(file);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado com o ID: " + productId));

        product.setPath(fileUrl);

        productRepository.save(product);
        return fileUrl;
    }
}