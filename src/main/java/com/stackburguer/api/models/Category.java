package com.stackburguer.api.models;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;


@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome da categoria é obrigatório no banco")
    private String name;

    private String path;

    public UUID getId(){ return id; }
    public void setId(UUID id){this.id = id;}
    public String getName(){return name;}
    public void setName(String name) {this.name = name;}
    public String getPath() {return path;}
    public void setPath(String path) {this.path = path;}
}
