package com.taskly.backend.model;

import jakarta.persistence.*;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;

    // Konstruktor Tanpa Argumen (NoArgsConstructor - Wajib untuk Hibernate/JPA)
    public Category() {}

    // Konstruktor Semua Argumen (AllArgsConstructor)
    public Category(Long id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    // GETTER & SETTER MANUAL (Solusi Mutlak Error getCategoryName)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
