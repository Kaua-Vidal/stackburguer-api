package com.stackburguer.api.models.order;

import com.stackburguer.api.models.Category;
import jakarta.persistence.Embeddable;

@Embeddable //Isso avisa ao postgres: "Crie colunas para isso em uma tabela dependente"
public class ProductItem{

    private Long id;
    private String name;
    private Double price;
    private String category;
    private String url;  //Link da imagem para o front
    private int quantity;

    public ProductItem() {}

    public ProductItem(Long id, String name, Double price, String category, String url, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.url = url;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

//Aqui, salvamos uma "Snapshot" do produto da hora da compra
//pois se o preço for alterado depois, o pedido continuará com o mesmo preço ainda
