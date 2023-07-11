package edu.oau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import edu.oau.IntegrationTest;
import edu.oau.domain.Comment;
import edu.oau.repository.CommentRepository;
import edu.oau.repository.EntityManager;
import edu.oau.service.dto.CommentDTO;
import edu.oau.service.mapper.CommentMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CommentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CommentResourceIT {

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Comment comment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createEntity(EntityManager em) {
        Comment comment = new Comment().createdDate(DEFAULT_CREATED_DATE).subject(DEFAULT_SUBJECT).comment(DEFAULT_COMMENT);
        return comment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createUpdatedEntity(EntityManager em) {
        Comment comment = new Comment().createdDate(UPDATED_CREATED_DATE).subject(UPDATED_SUBJECT).comment(UPDATED_COMMENT);
        return comment;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Comment.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        comment = createEntity(em);
    }

    @Test
    void createComment() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().collectList().block().size();
        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testComment.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testComment.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    void createCommentWithExistingId() throws Exception {
        // Create the Comment with an existing ID
        comment.setId(1L);
        CommentDTO commentDTO = commentMapper.toDto(comment);

        int databaseSizeBeforeCreate = commentRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllComments() {
        // Initialize the database
        commentRepository.save(comment).block();

        // Get all the commentList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(comment.getId().intValue()))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].subject")
            .value(hasItem(DEFAULT_SUBJECT))
            .jsonPath("$.[*].comment")
            .value(hasItem(DEFAULT_COMMENT));
    }

    @Test
    void getComment() {
        // Initialize the database
        commentRepository.save(comment).block();

        // Get the comment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, comment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(comment.getId().intValue()))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.subject")
            .value(is(DEFAULT_SUBJECT))
            .jsonPath("$.comment")
            .value(is(DEFAULT_COMMENT));
    }

    @Test
    void getNonExistingComment() {
        // Get the comment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingComment() throws Exception {
        // Initialize the database
        commentRepository.save(comment).block();

        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();

        // Update the comment
        Comment updatedComment = commentRepository.findById(comment.getId()).block();
        updatedComment.createdDate(UPDATED_CREATED_DATE).subject(UPDATED_SUBJECT).comment(UPDATED_COMMENT);
        CommentDTO commentDTO = commentMapper.toDto(updatedComment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testComment.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testComment.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    void putNonExistingComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(count.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(count.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(count.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        commentRepository.save(comment).block();

        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();

        // Update the comment using partial update
        Comment partialUpdatedComment = new Comment();
        partialUpdatedComment.setId(comment.getId());

        partialUpdatedComment.createdDate(UPDATED_CREATED_DATE).comment(UPDATED_COMMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedComment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testComment.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testComment.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    void fullUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        commentRepository.save(comment).block();

        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();

        // Update the comment using partial update
        Comment partialUpdatedComment = new Comment();
        partialUpdatedComment.setId(comment.getId());

        partialUpdatedComment.createdDate(UPDATED_CREATED_DATE).subject(UPDATED_SUBJECT).comment(UPDATED_COMMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedComment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testComment.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testComment.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    void patchNonExistingComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(count.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, commentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(count.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().collectList().block().size();
        comment.setId(count.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteComment() {
        // Initialize the database
        commentRepository.save(comment).block();

        int databaseSizeBeforeDelete = commentRepository.findAll().collectList().block().size();

        // Delete the comment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, comment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Comment> commentList = commentRepository.findAll().collectList().block();
        assertThat(commentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
