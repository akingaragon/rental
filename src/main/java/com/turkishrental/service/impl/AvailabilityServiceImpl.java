package com.turkishrental.service.impl;

import com.turkishrental.domain.Availability;
import com.turkishrental.repository.AvailabilityRepository;
import com.turkishrental.service.AvailabilityService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.turkishrental.domain.Availability}.
 */
@Service
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {

    private final Logger log = LoggerFactory.getLogger(AvailabilityServiceImpl.class);

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public Availability save(Availability availability) {
        log.debug("Request to save Availability : {}", availability);
        return availabilityRepository.save(availability);
    }

    @Override
    public Availability update(Availability availability) {
        log.debug("Request to update Availability : {}", availability);
        return availabilityRepository.save(availability);
    }

    @Override
    public Optional<Availability> partialUpdate(Availability availability) {
        log.debug("Request to partially update Availability : {}", availability);

        return availabilityRepository
            .findById(availability.getId())
            .map(existingAvailability -> {
                if (availability.getDate() != null) {
                    existingAvailability.setDate(availability.getDate());
                }

                return existingAvailability;
            })
            .map(availabilityRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Availability> findAll() {
        log.debug("Request to get all Availabilities");
        return availabilityRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Availability> findOne(Long id) {
        log.debug("Request to get Availability : {}", id);
        return availabilityRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Availability : {}", id);
        availabilityRepository.deleteById(id);
    }
}
