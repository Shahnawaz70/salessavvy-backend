package com.salessavvy.app.common.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer productId;
	
	@Column(nullable = false)
	private String name;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;
	
	@Column(nullable = false)
	private Integer stock;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private Category category;
	
	@Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;
	
	@Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;
	
	public Product() {
		// TODO Auto-generated constructor stub
	}

	public Product(Integer productId, String name, String description, BigDecimal price, Integer stock,
			Category category, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.productId = productId;
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.category = category;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Product(String name, String description, BigDecimal price, Integer stock, Category category,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.category = category;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Product [productId=" + productId + ", name=" + name + ", description=" + description + ", price="
				+ price + ", stock=" + stock + ", category=" + category + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, createdAt, description, name, price, productId, stock, updatedAt);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return Objects.equals(category, other.category) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(description, other.description) && Objects.equals(name, other.name)
				&& Objects.equals(price, other.price) && Objects.equals(productId, other.productId)
				&& Objects.equals(stock, other.stock) && Objects.equals(updatedAt, other.updatedAt);
	}
	
}
