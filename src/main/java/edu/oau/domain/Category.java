package edu.oau.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Category.
 */
@Table("category")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("code")
    private String code;

    @Column("description")
    private String description;

    @Transient
    @JsonIgnoreProperties(value = { "subcategories", "parent", "category" }, allowSetters = true)
    private Set<Category> subcategories = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "subcategories", "parent", "category" }, allowSetters = true)
    private Category parent;

    @Transient
    @JsonIgnoreProperties(value = { "subcategories", "parent", "category" }, allowSetters = true)
    private Category category;

    @Column("parent_id")
    private Long parentId;

    @Column("category_id")
    private Long categoryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Category code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public Category description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Category> getSubcategories() {
        return this.subcategories;
    }

    public void setSubcategories(Set<Category> categories) {
        if (this.subcategories != null) {
            this.subcategories.forEach(i -> i.setCategory(null));
        }
        if (categories != null) {
            categories.forEach(i -> i.setCategory(this));
        }
        this.subcategories = categories;
    }

    public Category subcategories(Set<Category> categories) {
        this.setSubcategories(categories);
        return this;
    }

    public Category addSubcategory(Category category) {
        this.subcategories.add(category);
        category.setCategory(this);
        return this;
    }

    public Category removeSubcategory(Category category) {
        this.subcategories.remove(category);
        category.setCategory(null);
        return this;
    }

    public Category getParent() {
        return this.parent;
    }

    public void setParent(Category category) {
        this.parent = category;
        this.parentId = category != null ? category.getId() : null;
    }

    public Category parent(Category category) {
        this.setParent(category);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
    }

    public Category category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long category) {
        this.parentId = category;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long category) {
        this.categoryId = category;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
