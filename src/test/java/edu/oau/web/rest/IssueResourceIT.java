package edu.oau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import edu.oau.IntegrationTest;
import edu.oau.domain.Issue;
import edu.oau.domain.enumeration.CLASSIFICATION;
import edu.oau.domain.enumeration.ENTRYCHANNEL;
import edu.oau.domain.enumeration.ISSUESTATUS;
import edu.oau.repository.EntityManager;
import edu.oau.repository.IssueRepository;
import edu.oau.service.dto.IssueDTO;
import edu.oau.service.mapper.IssueMapper;
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
 * Integration tests for the {@link IssueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class IssueResourceIT {

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ISSUESTATUS DEFAULT_STATUS = ISSUESTATUS.NEW;
    private static final ISSUESTATUS UPDATED_STATUS = ISSUESTATUS.CLOSED;

    private static final CLASSIFICATION DEFAULT_CLASSIFICATION = CLASSIFICATION.COMPLAINT;
    private static final CLASSIFICATION UPDATED_CLASSIFICATION = CLASSIFICATION.ENQUIRY;

    private static final ENTRYCHANNEL DEFAULT_ENTRY_CHANNEL = ENTRYCHANNEL.WHATSAPP;
    private static final ENTRYCHANNEL UPDATED_ENTRY_CHANNEL = ENTRYCHANNEL.MESSANGER;

    private static final String ENTITY_API_URL = "/api/issues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Issue issue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Issue createEntity(EntityManager em) {
        Issue issue = new Issue()
            .createdDate(DEFAULT_CREATED_DATE)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .classification(DEFAULT_CLASSIFICATION)
            .entryChannel(DEFAULT_ENTRY_CHANNEL);
        return issue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Issue createUpdatedEntity(EntityManager em) {
        Issue issue = new Issue()
            .createdDate(UPDATED_CREATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .classification(UPDATED_CLASSIFICATION)
            .entryChannel(UPDATED_ENTRY_CHANNEL);
        return issue;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Issue.class).block();
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
        issue = createEntity(em);
    }

    @Test
    void createIssue() throws Exception {
        int databaseSizeBeforeCreate = issueRepository.findAll().collectList().block().size();
        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issueDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate + 1);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testIssue.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testIssue.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testIssue.getClassification()).isEqualTo(DEFAULT_CLASSIFICATION);
        assertThat(testIssue.getEntryChannel()).isEqualTo(DEFAULT_ENTRY_CHANNEL);
    }

    @Test
    void createIssueWithExistingId() throws Exception {
        // Create the Issue with an existing ID
        issue.setId(1L);
        IssueDTO issueDTO = issueMapper.toDto(issue);

        int databaseSizeBeforeCreate = issueRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllIssues() {
        // Initialize the database
        issueRepository.save(issue).block();

        // Get all the issueList
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
            .value(hasItem(issue.getId().intValue()))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].classification")
            .value(hasItem(DEFAULT_CLASSIFICATION.toString()))
            .jsonPath("$.[*].entryChannel")
            .value(hasItem(DEFAULT_ENTRY_CHANNEL.toString()));
    }

    @Test
    void getIssue() {
        // Initialize the database
        issueRepository.save(issue).block();

        // Get the issue
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, issue.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(issue.getId().intValue()))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.classification")
            .value(is(DEFAULT_CLASSIFICATION.toString()))
            .jsonPath("$.entryChannel")
            .value(is(DEFAULT_ENTRY_CHANNEL.toString()));
    }

    @Test
    void getNonExistingIssue() {
        // Get the issue
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingIssue() throws Exception {
        // Initialize the database
        issueRepository.save(issue).block();

        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();

        // Update the issue
        Issue updatedIssue = issueRepository.findById(issue.getId()).block();
        updatedIssue
            .createdDate(UPDATED_CREATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .classification(UPDATED_CLASSIFICATION)
            .entryChannel(UPDATED_ENTRY_CHANNEL);
        IssueDTO issueDTO = issueMapper.toDto(updatedIssue);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, issueDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issueDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIssue.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testIssue.getClassification()).isEqualTo(UPDATED_CLASSIFICATION);
        assertThat(testIssue.getEntryChannel()).isEqualTo(UPDATED_ENTRY_CHANNEL);
    }

    @Test
    void putNonExistingIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, issueDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(issueDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateIssueWithPatch() throws Exception {
        // Initialize the database
        issueRepository.save(issue).block();

        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();

        // Update the issue using partial update
        Issue partialUpdatedIssue = new Issue();
        partialUpdatedIssue.setId(issue.getId());

        partialUpdatedIssue.description(UPDATED_DESCRIPTION).status(UPDATED_STATUS).entryChannel(UPDATED_ENTRY_CHANNEL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIssue.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedIssue))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIssue.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testIssue.getClassification()).isEqualTo(DEFAULT_CLASSIFICATION);
        assertThat(testIssue.getEntryChannel()).isEqualTo(UPDATED_ENTRY_CHANNEL);
    }

    @Test
    void fullUpdateIssueWithPatch() throws Exception {
        // Initialize the database
        issueRepository.save(issue).block();

        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();

        // Update the issue using partial update
        Issue partialUpdatedIssue = new Issue();
        partialUpdatedIssue.setId(issue.getId());

        partialUpdatedIssue
            .createdDate(UPDATED_CREATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .classification(UPDATED_CLASSIFICATION)
            .entryChannel(UPDATED_ENTRY_CHANNEL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIssue.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedIssue))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIssue.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testIssue.getClassification()).isEqualTo(UPDATED_CLASSIFICATION);
        assertThat(testIssue.getEntryChannel()).isEqualTo(UPDATED_ENTRY_CHANNEL);
    }

    @Test
    void patchNonExistingIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, issueDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(issueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(issueDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().collectList().block().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(issueDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteIssue() {
        // Initialize the database
        issueRepository.save(issue).block();

        int databaseSizeBeforeDelete = issueRepository.findAll().collectList().block().size();

        // Delete the issue
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, issue.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Issue> issueList = issueRepository.findAll().collectList().block();
        assertThat(issueList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
