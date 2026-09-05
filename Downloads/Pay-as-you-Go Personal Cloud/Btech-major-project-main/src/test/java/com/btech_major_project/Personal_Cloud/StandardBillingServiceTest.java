package com.btech_major_project.Personal_Cloud;

import com.btech_major_project.Personal_Cloud.dto.BillingSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class StandardBillingServiceTest {

    private StandardBillingService billingService;
    private UserUsageRepository usageRepo;

    @BeforeEach
    void setUp() {
        usageRepo = Mockito.mock(UserUsageRepository.class);
        billingService = new StandardBillingService(usageRepo);
    }

    @Test
    void testCalculateCurrent_NoUsage() {
        User user = new User();
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);

        when(usageRepo.findByUserId(1L)).thenReturn(Optional.empty());

        BillingSummary summary = billingService.calculateCurrent(user);

        assertNotNull(summary);
        assertEquals(0L, summary.getStorageBytes());
        assertEquals(0, summary.getTotal().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testCalculateCurrent_WithUsage() {
        User user = new User();
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);

        UserUsage usage = new UserUsage();
        usage.setTotalBytesStored(1024L * 1024L * 1024L * 10L); // 10 GB
        usage.setPutCount(1000L); // 1000 requests
        usage.setGetCount(2000L); // 2000 requests
        usage.setListCount(50L); // 50 requests

        when(usageRepo.findByUserId(1L)).thenReturn(Optional.of(usage));

        BillingSummary summary = billingService.calculateCurrent(user);

        assertNotNull(summary);
        assertEquals(10L * 1024L * 1024L * 1024L, summary.getStorageBytes());
        // 10 GB * $0.025 = $0.25
        assertEquals(0, summary.getStorageCost().compareTo(new BigDecimal("0.25")));
        // 1000 PUT + 50 LIST = 1050 write requests. 1050 * ($0.0005 / 1000) = $0.000525
        assertEquals(0, summary.getWriteCost().compareTo(new BigDecimal("0.000525")));
        // 2000 GET requests. 2000 * ($0.0004 / 1000) = $0.0008
        assertEquals(0, summary.getReadCost().compareTo(new BigDecimal("0.0008")));
        // Total = $0.25 + $0.000525 + $0.0008 = $0.251325
        assertEquals(0, summary.getTotal().stripTrailingZeros().compareTo(new BigDecimal("0.251325").stripTrailingZeros()));
    }
}
