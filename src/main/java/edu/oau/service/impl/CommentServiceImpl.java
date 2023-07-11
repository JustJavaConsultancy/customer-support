package edu.oau.service.impl;

import edu.oau.domain.Comment;
import edu.oau.repository.CommentRepository;
import edu.oau.service.CommentService;
import edu.oau.service.dto.CommentDTO;
import edu.oau.service.mapper.CommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Comment}.
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public Mono<CommentDTO> save(CommentDTO commentDTO) {
        log.debug("Request to save Comment : {}", commentDTO);
        return commentRepository.save(commentMapper.toEntity(commentDTO)).map(commentMapper::toDto);
    }

    @Override
    public Mono<CommentDTO> update(CommentDTO commentDTO) {
        log.debug("Request to update Comment : {}", commentDTO);
        return commentRepository.save(commentMapper.toEntity(commentDTO)).map(commentMapper::toDto);
    }

    @Override
    public Mono<CommentDTO> partialUpdate(CommentDTO commentDTO) {
        log.debug("Request to partially update Comment : {}", commentDTO);

        return commentRepository
            .findById(commentDTO.getId())
            .map(existingComment -> {
                commentMapper.partialUpdate(existingComment, commentDTO);

                return existingComment;
            })
            .flatMap(commentRepository::save)
            .map(commentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CommentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Comments");
        return commentRepository.findAllBy(pageable).map(commentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return commentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CommentDTO> findOne(Long id) {
        log.debug("Request to get Comment : {}", id);
        return commentRepository.findById(id).map(commentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Comment : {}", id);
        return commentRepository.deleteById(id);
    }
}
