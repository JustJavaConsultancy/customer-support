package edu.oau.service.mapper;

import edu.oau.domain.Category;
import edu.oau.domain.Customer;
import edu.oau.domain.Issue;
import edu.oau.service.dto.CategoryDTO;
import edu.oau.service.dto.CustomerDTO;
import edu.oau.service.dto.IssueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Issue} and its DTO {@link IssueDTO}.
 */
@Mapper(componentModel = "spring")
public interface IssueMapper extends EntityMapper<IssueDTO, Issue> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    IssueDTO toDto(Issue s);

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoryDTO toDtoCategoryId(Category category);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
