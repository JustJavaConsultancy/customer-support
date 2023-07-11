package edu.oau.service.impl;

import edu.oau.domain.Issue;
import edu.oau.repository.IssueRepository;
import edu.oau.service.IssueService;
import edu.oau.service.dto.IssueDTO;
import edu.oau.service.mapper.IssueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Issue}.
 */
@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    private final Logger log = LoggerFactory.getLogger(IssueServiceImpl.class);

    private final IssueRepository issueRepository;

    private final IssueMapper issueMapper;

    public IssueServiceImpl(IssueRepository issueRepository, IssueMapper issueMapper) {
        this.issueRepository = issueRepository;
        this.issueMapper = issueMapper;
    }

    @Override
    public Mono<IssueDTO> save(IssueDTO issueDTO) {
        log.debug("Request to save Issue : {}", issueDTO);
        return issueRepository.save(issueMapper.toEntity(issueDTO)).map(issueMapper::toDto);
    }

    @Override
    public Mono<IssueDTO> update(IssueDTO issueDTO) {
        log.debug("Request to update Issue : {}", issueDTO);
        return issueRepository.save(issueMapper.toEntity(issueDTO)).map(issueMapper::toDto);
    }

    @Override
    public Mono<IssueDTO> partialUpdate(IssueDTO issueDTO) {
        log.debug("Request to partially update Issue : {}", issueDTO);

        return issueRepository
            .findById(issueDTO.getId())
            .map(existingIssue -> {
                issueMapper.partialUpdate(existingIssue, issueDTO);

                return existingIssue;
            })
            .flatMap(issueRepository::save)
            .map(issueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<IssueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Issues");
        return issueRepository.findAllBy(pageable).map(issueMapper::toDto);
    }

    public Mono<Long> countAll() {
        return issueRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<IssueDTO> findOne(Long id) {
        log.debug("Request to get Issue : {}", id);
        return issueRepository.findById(id).map(issueMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Issue : {}", id);
        return issueRepository.deleteById(id);
    }
}
