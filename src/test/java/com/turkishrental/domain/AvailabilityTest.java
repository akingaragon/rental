package com.turkishrental.domain;

import static com.turkishrental.domain.AvailabilityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.turkishrental.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AvailabilityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Availability.class);
        Availability availability1 = getAvailabilitySample1();
        Availability availability2 = new Availability();
        assertThat(availability1).isNotEqualTo(availability2);

        availability2.setId(availability1.getId());
        assertThat(availability1).isEqualTo(availability2);

        availability2 = getAvailabilitySample2();
        assertThat(availability1).isNotEqualTo(availability2);
    }
}
