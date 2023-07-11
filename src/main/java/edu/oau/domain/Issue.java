package edu.oau.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.oau.domain.enumeration.CLASSIFICATION;
import edu.oau.domain.enumeration.ENTRYCHANNEL;
import edu.oau.domain.enumeration.ISSUESTATUS;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Issue.
 */
@Table("issue")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Issue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("created_date")
    private LocalDate createdDate;

    @Column("description")
    private String description;

    @Column("status")
    private ISSUESTATUS status;

    @Column("classification")
    private CLASSIFICATION classification;

    @Column("entry_channel")
    private ENTRYCHANNEL entryChannel;

    @Transient
    @JsonIgnoreProperties(value = { "issue" }, allowSetters = true)
    private Set<Comment> comments = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "subcategories", "parent", "category" }, allowSetters = true)
    private Category category;

    @Transient
    private Customer customer;

    @Column("category_id")
    private Long categoryId;

    @Column("customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Issue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public Issue createdDate(LocalDate createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return this.description;
    }

    public Issue description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ISSUESTATUS getStatus() {
        return this.status;
    }

    public Issue status(ISSUESTATUS status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ISSUESTATUS status) {
        this.status = status;
    }

    public CLASSIFICATION getClassification() {
        return this.classification;
    }

    public Issue classification(CLASSIFICATION classification) {
        this.setClassification(classification);
        return this;
    }

    public void setClassification(CLASSIFICATION classification) {
        this.classification = classification;
    }

    public ENTRYCHANNEL getEntryChannel() {
        return this.entryChannel;
    }

    public Issue entryChannel(ENTRYCHANNEL entryChannel) {
        this.setEntryChannel(entryChannel);
        return this;
    }

    public void setEntryChannel(ENTRYCHANNEL entryChannel) {
        this.entryChannel = entryChannel;
    }

    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setIssue(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setIssue(this));
        }
        this.comments = comments;
    }

    public Issue comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Issue addComments(Comment comment) {
        this.comments.add(comment);
        comment.setIssue(this);
        return this;
    }

    public Issue removeComments(Comment comment) {
        this.comments.remove(comment);
        comment.setIssue(null);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
    }

    public Issue category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public Issue customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long category) {
        this.categoryId = category;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Issue)) {
            return false;
        }
        return id != null && id.equals(((Issue) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Issue{" +
            "id=" + getId() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", classification='" + getClassification() + "'" +
            ", entryChannel='" + getEntryChannel() + "'" +
            "}";
    }
}
