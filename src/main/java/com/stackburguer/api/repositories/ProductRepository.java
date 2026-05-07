package com.stackburguer.api.repositories;

import com.stackburguer.api.models.Category;
import com.stackburguer.api.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    // O spring já transforma isso em SELECT * FROM products WHERE category_id = ?
    List<Product> findByCategoryId(String categoryId);
}
