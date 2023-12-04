package com.turkishrental.repository;

import com.turkishrental.domain.Availability;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Availability entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {}