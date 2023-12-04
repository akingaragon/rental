package com.turkishrental.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AvailabilityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Availability getAvailabilitySample1() {
        return new Availability().id(1L).date("date1");
    }

    public static Availability getAvailabilitySample2() {
        return new Availability().id(2L).date("date2");
    }

    public static Availability getAvailabilityRandomSampleGenerator() {
        return new Availability().id(longCount.incrementAndGet()).date(UUID.randomUUID().toString());
    }
}
