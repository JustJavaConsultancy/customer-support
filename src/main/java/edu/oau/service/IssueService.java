package edu.oau.service;

import edu.oau.service.dto.IssueDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link edu.oau.domain.Issue}.
 */
public interface IssueService {
    /**
     * Save a issue.
     *
     * @param issueDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<IssueDTO> save(IssueDTO issueDTO);

    /**
     * Updates a issue.
     *
     * @param issueDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<IssueDTO> update(IssueDTO issueDTO);

    /**
     * Partially updates a issue.
     *
     * @param issueDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<IssueDTO> partialUpdate(IssueDTO issueDTO);

    /**
     * Get all the issues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<IssueDTO> findAll(Pageable pageable);

    /**
     * Returns the number of issues available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" issue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<IssueDTO> findOne(Long id);

    /**
     * Delete the "id" issue.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
