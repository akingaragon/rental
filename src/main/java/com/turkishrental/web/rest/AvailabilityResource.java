package com.turkishrental.web.rest;

import com.turkishrental.domain.Availability;
import com.turkishrental.repository.AvailabilityRepository;
import com.turkishrental.service.AvailabilityService;
import com.turkishrental.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.turkishrental.domain.Availability}.
 */
@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityResource {

    private final Logger log = LoggerFactory.getLogger(AvailabilityResource.class);

    private static final String ENTITY_NAME = "availability";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AvailabilityService availabilityService;

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityResource(AvailabilityService availabilityService, AvailabilityRepository availabilityRepository) {
        this.availabilityService = availabilityService;
        this.availabilityRepository = availabilityRepository;
    }

    /**
     * {@code POST  /availabilities} : Create a new availability.
     *
     * @param availability the availability to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new availability, or with status {@code 400 (Bad Request)} if the availability has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Availability> createAvailability(@RequestBody Availability availability) throws URISyntaxException {
        log.debug("REST request to save Availability : {}", availability);
        if (availability.getId() != null) {
            throw new BadRequestAlertException("A new availability cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Availability result = availabilityService.save(availability);
        return ResponseEntity
            .created(new URI("/api/availabilities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /availabilities/:id} : Updates an existing availability.
     *
     * @param id the id of the availability to save.
     * @param availability the availability to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated availability,
     * or with status {@code 400 (Bad Request)} if the availability is not valid,
     * or with status {@code 500 (Internal Server Error)} if the availability couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Availability> updateAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Availability availability
    ) throws URISyntaxException {
        log.debug("REST request to update Availability : {}, {}", id, availability);
        if (availability.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, availability.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!availabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Availability result = availabilityService.update(availability);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, availability.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /availabilities/:id} : Partial updates given fields of an existing availability, field will ignore if it is null
     *
     * @param id the id of the availability to save.
     * @param availability the availability to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated availability,
     * or with status {@code 400 (Bad Request)} if the availability is not valid,
     * or with status {@code 404 (Not Found)} if the availability is not found,
     * or with status {@code 500 (Internal Server Error)} if the availability couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Availability> partialUpdateAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Availability availability
    ) throws URISyntaxException {
        log.debug("REST request to partial update Availability partially : {}, {}", id, availability);
        if (availability.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, availability.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!availabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Availability> result = availabilityService.partialUpdate(availability);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, availability.getId().toString())
        );
    }

    /**
     * {@code GET  /availabilities} : get all the availabilities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of availabilities in body.
     */
    @GetMapping("")
    public List<Availability> getAllAvailabilities() {
        log.debug("REST request to get all Availabilities");
        return availabilityService.findAll();
    }

    /**
     * {@code GET  /availabilities/:id} : get the "id" availability.
     *
     * @param id the id of the availability to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the availability, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Availability> getAvailability(@PathVariable Long id) {
        log.debug("REST request to get Availability : {}", id);
        Optional<Availability> availability = availabilityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(availability);
    }

    /**
     * {@code DELETE  /availabilities/:id} : delete the "id" availability.
     *
     * @param id the id of the availability to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        log.debug("REST request to delete Availability : {}", id);
        availabilityService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
