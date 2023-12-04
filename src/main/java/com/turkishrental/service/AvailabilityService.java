package com.turkishrental.service;

import com.turkishrental.domain.Availability;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.turkishrental.domain.Availability}.
 */
public interface AvailabilityService {
    /**
     * Save a availability.
     *
     * @param availability the entity to save.
     * @return the persisted entity.
     */
    Availability save(Availability availability);

    /**
     * Updates a availability.
     *
     * @param availability the entity to update.
     * @return the persisted entity.
     */
    Availability update(Availability availability);

    /**
     * Partially updates a availability.
     *
     * @param availability the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Availability> partialUpdate(Availability availability);

    /**
     * Get all the availabilities.
     *
     * @return the list of entities.
     */
    List<Availability> findAll();

    /**
     * Get the "id" availability.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Availability> findOne(Long id);

    /**
     * Delete the "id" availability.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
