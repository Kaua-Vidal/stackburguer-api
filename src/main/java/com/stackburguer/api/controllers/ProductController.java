package com.stackburguer.api.controllers;


import com.stackburguer.api.DTO.product.ProductResponseDTO;
import com.stackburguer.api.service.ProductService;
import com.stackburguer.api.utils.S3Util;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")  //todas minhas rotas começam com /products
public class ProductController {

    @Autowired
    private ProductService service;

    private final S3Util s3Util;

    public ProductController(S3Util s3Util){
        this.s3Util = s3Util;
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        try {
            List<ProductResponseDTO> products = service.getAllProducts();
            return ResponseEntity.ok(products);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar produtos.");
        }


    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ProductResponseDTO> create(
            @Valid
            @RequestPart("product") String productJson,
            @RequestPart("file") MultipartFile file
    ) throws IOException{
            ProductResponseDTO response = service.createProduct(productJson, file);

            URI uri = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(response.id())
                    .toUri();
            return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getByCategoryId(@PathVariable UUID categoryId){
        List<ProductResponseDTO> products = service.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }


    @DeleteMapping("/{id}")  //Serve para quando for chamar o delete: /products/5, o 5 já vai ser o ID
    public ResponseEntity<Void> delete(@PathVariable Long id) throws RuntimeException{
            //O Service realiza a exclusão
            service.deleteProduct(id);

            // No sucesso, retornamos o status 204
            // o .build() cria um ResponseEntity sem corpo
            return ResponseEntity.noContent().build();  //204 No Content
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
            ProductResponseDTO response = service.updateProduct(id, productJson, file);
            return ResponseEntity.ok(response);
    }

    @PostMapping("/uploads")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        String fileUrl = s3Util.uploadFile(file);
        return ResponseEntity.ok(fileUrl);
    }

    @PostMapping("/uploads/{id}")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file){
        String imageUrl = service.updateProductImage(id, file);

        return ResponseEntity.ok(imageUrl);
    }

}
