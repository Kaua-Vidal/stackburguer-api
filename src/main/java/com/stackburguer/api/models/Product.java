package com.stackburguer.api.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity      //Etiqueta: Isso é uma tabela no bando de dados
@Table(name = "products")
@Data        //Etiqueta do Lombok: Cria getters, setters e toString sozinha
public class Product {
    @Id               //Etiqueta: Essa é a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // Auto-incremento
    private Long id;

    @NotNull(message = "O ID da cateogria é obrigatório")
    @ManyToOne
    @JoinColumn(name = "category_id") //nome da coluna no banco SQL
    private Category category;

    @NotBlank(message = "O nome do produto não pode ser nulo no banco")
    private String name;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser positivo")
    private double price;

    private String path;

    private boolean offer = false;
}
