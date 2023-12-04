package com.turkishrental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.turkishrental.IntegrationTest;
import com.turkishrental.domain.Availability;
import com.turkishrental.repository.AvailabilityRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AvailabilityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AvailabilityResourceIT {

    private static final String DEFAULT_DATE = "AAAAAAAAAA";
    private static final String UPDATED_DATE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/availabilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAvailabilityMockMvc;

    private Availability availability;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Availability createEntity(EntityManager em) {
        Availability availability = new Availability().date(DEFAULT_DATE);
        return availability;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Availability createUpdatedEntity(EntityManager em) {
        Availability availability = new Availability().date(UPDATED_DATE);
        return availability;
    }

    @BeforeEach
    public void initTest() {
        availability = createEntity(em);
    }

    @Test
    @Transactional
    void createAvailability() throws Exception {
        int databaseSizeBeforeCreate = availabilityRepository.findAll().size();
        // Create the Availability
        restAvailabilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(availability)))
            .andExpect(status().isCreated());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeCreate + 1);
        Availability testAvailability = availabilityList.get(availabilityList.size() - 1);
        assertThat(testAvailability.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createAvailabilityWithExistingId() throws Exception {
        // Create the Availability with an existing ID
        availability.setId(1L);

        int databaseSizeBeforeCreate = availabilityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAvailabilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(availability)))
            .andExpect(status().isBadRequest());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAvailabilities() throws Exception {
        // Initialize the database
        availabilityRepository.saveAndFlush(availability);

        // Get all the availabilityList
        restAvailabilityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(availability.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE)));
    }

    @Test
    @Transactional
    void getAvailability() throws Exception {
        // Initialize the database
        availabilityRepository.saveAndFlush(availability);

        // Get the availability
        restAvailabilityMockMvc
            .perform(get(ENTITY_API_URL_ID, availability.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(availability.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE));
    }

    @Test
    @Transactional
    void getNonExistingAvailability() throws Exception {
        // Get the availability
        restAvailabilityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAvailability() throws Exception {
        // Initialize the database
        availabilityRepository.saveAndFlush(availability);

        int databaseSizeBeforeUpdate = availabilityRepository.findAll().size();

        // Update the availability
        Availability updatedAvailability = availabilityRepository.findById(availability.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAvailability are not directly saved in db
        em.detach(updatedAvailability);
        updatedAvailability.date(UPDATED_DATE);

        restAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAvailability.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAvailability))
            )
            .andExpect(status().isOk());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeUpdate);
        Availability testAvailability = availabilityList.get(availabilityList.size() - 1);
        assertThat(testAvailability.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingAvailability() throws Exception {
        int databaseSizeBeforeUpdate = availabilityRepository.findAll().size();
        availability.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, availability.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(availability))
            )
            .andExpect(status().isBadRequest());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAvailability() throws Exception {
        int databaseSizeBeforeUpdate = availabilityRepository.findAll().size();
        availability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvailabilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(availability))
            )
            .andExpect(status().isBadRequest());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAvailability() throws Exception {
        int databaseSizeBeforeUpdate = availabilityRepository.findAll().size();
        availability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvailabilityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(availability)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAvailabilityWithPatch() throws Exception {
        // Initialize the database
        availabilityRepository.saveAndFlush(availability);

        int databaseSizeBeforeUpdate = availabilityRepository.findAll().size();

        // Update the availability using partial update
        Availability partialUpdatedAvailability = new Availability();
        partialUpdatedAvailability.setId(availability.getId());

        restAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAvailability))
            )
            .andExpect(status().isOk());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeUpdate);
        Availability testAvailability = availabilityList.get(availabilityList.size() - 1);
        assertThat(testAvailability.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void fullUpdateAvailabilityWithPatch() throws Exception {
        // Initialize the database
        availabilityRepository.saveAndFlush(availability);

        int databaseSizeBeforeUpdate = availabilityRepository.findAll().size();

        // Update the availability using partial update
        Availability partialUpdatedAvailability = new Availability();
        partialUpdatedAvailability.setId(availability.getId());

        partialUpdatedAvailability.date(UPDATED_DATE);

        restAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAvailability.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAvailability))
            )
            .andExpect(status().isOk());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeUpdate);
        Availability testAvailability = availabilityList.get(availabilityList.size() - 1);
        assertThat(testAvailability.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingAvailability() throws Exception {
        int databaseSizeBeforeUpdate = availabilityRepository.findAll().size();
        availability.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, availability.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(availability))
            )
            .andExpect(status().isBadRequest());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAvailability() throws Exception {
        int databaseSizeBeforeUpdate = availabilityRepository.findAll().size();
        availability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(availability))
            )
            .andExpect(status().isBadRequest());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAvailability() throws Exception {
        int databaseSizeBeforeUpdate = availabilityRepository.findAll().size();
        availability.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAvailabilityMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(availability))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Availability in the database
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAvailability() throws Exception {
        // Initialize the database
        availabilityRepository.saveAndFlush(availability);

        int databaseSizeBeforeDelete = availabilityRepository.findAll().size();

        // Delete the availability
        restAvailabilityMockMvc
            .perform(delete(ENTITY_API_URL_ID, availability.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Availability> availabilityList = availabilityRepository.findAll();
        assertThat(availabilityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
