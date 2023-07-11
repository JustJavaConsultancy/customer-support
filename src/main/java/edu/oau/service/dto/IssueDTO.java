package edu.oau.service.dto;

import edu.oau.domain.enumeration.CLASSIFICATION;
import edu.oau.domain.enumeration.ENTRYCHANNEL;
import edu.oau.domain.enumeration.ISSUESTATUS;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link edu.oau.domain.Issue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IssueDTO implements Serializable {

    private Long id;

    private LocalDate createdDate;

    private String description;

    private ISSUESTATUS status;

    private CLASSIFICATION classification;

    private ENTRYCHANNEL entryChannel;

    private CategoryDTO category;

    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ISSUESTATUS getStatus() {
        return status;
    }

    public void setStatus(ISSUESTATUS status) {
        this.status = status;
    }

    public CLASSIFICATION getClassification() {
        return classification;
    }

    public void setClassification(CLASSIFICATION classification) {
        this.classification = classification;
    }

    public ENTRYCHANNEL getEntryChannel() {
        return entryChannel;
    }

    public void setEntryChannel(ENTRYCHANNEL entryChannel) {
        this.entryChannel = entryChannel;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IssueDTO)) {
            return false;
        }

        IssueDTO issueDTO = (IssueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, issueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IssueDTO{" +
            "id=" + getId() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", classification='" + getClassification() + "'" +
            ", entryChannel='" + getEntryChannel() + "'" +
            ", category=" + getCategory() +
            ", customer=" + getCustomer() +
            "}";
    }
}
