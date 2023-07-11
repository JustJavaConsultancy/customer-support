package edu.oau.service.mapper;

import edu.oau.domain.Comment;
import edu.oau.domain.Issue;
import edu.oau.service.dto.CommentDTO;
import edu.oau.service.dto.IssueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "issue", source = "issue", qualifiedByName = "issueId")
    CommentDTO toDto(Comment s);

    @Named("issueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    IssueDTO toDtoIssueId(Issue issue);
}
