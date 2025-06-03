package com.pinterest.businessservice.repository;

import com.pinterest.businessservice.model.BusinessProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BusinessProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BusinessProfileRepository businessProfileRepository;

    @Test
    @DisplayName("Should find business profile by user ID")
    void shouldFindBusinessProfileByUserId() {
        // given
        BusinessProfile businessProfile = BusinessProfile.builder()
                .userId(101L)
                .businessName("Test Business")
                .description("Test Description")
                .verificationStatus(BusinessProfile.VerificationStatus.PENDING)
                .active(true)
                .build();
        
        entityManager.persist(businessProfile);
        entityManager.flush();

        // when
        Optional<BusinessProfile> found = businessProfileRepository.findByUserId(101L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getBusinessName()).isEqualTo("Test Business");
    }

    @Test
    @DisplayName("Should find business profiles by active status")
    void shouldFindBusinessProfilesByActiveStatus() {
        // given
        BusinessProfile activeProfile = BusinessProfile.builder()
                .userId(101L)
                .businessName("Active Business")
                .verificationStatus(BusinessProfile.VerificationStatus.VERIFIED)
                .active(true)
                .build();
        
        BusinessProfile inactiveProfile = BusinessProfile.builder()
                .userId(102L)
                .businessName("Inactive Business")
                .verificationStatus(BusinessProfile.VerificationStatus.VERIFIED)
                .active(false)
                .build();
        
        entityManager.persist(activeProfile);
        entityManager.persist(inactiveProfile);
        entityManager.flush();

        // when
        List<BusinessProfile> activeProfiles = businessProfileRepository.findByActive(true);
        List<BusinessProfile> inactiveProfiles = businessProfileRepository.findByActive(false);

        // then
        assertThat(activeProfiles).hasSize(1);
        assertThat(activeProfiles.get(0).getBusinessName()).isEqualTo("Active Business");
        
        assertThat(inactiveProfiles).hasSize(1);
        assertThat(inactiveProfiles.get(0).getBusinessName()).isEqualTo("Inactive Business");
    }

    @Test
    @DisplayName("Should find business profiles by verification status")
    void shouldFindBusinessProfilesByVerificationStatus() {
        // given
        BusinessProfile verifiedProfile = BusinessProfile.builder()
                .userId(101L)
                .businessName("Verified Business")
                .verificationStatus(BusinessProfile.VerificationStatus.VERIFIED)
                .active(true)
                .build();
        
        BusinessProfile pendingProfile = BusinessProfile.builder()
                .userId(102L)
                .businessName("Pending Business")
                .verificationStatus(BusinessProfile.VerificationStatus.PENDING)
                .active(true)
                .build();
        
        entityManager.persist(verifiedProfile);
        entityManager.persist(pendingProfile);
        entityManager.flush();

        // when
        List<BusinessProfile> verifiedProfiles = businessProfileRepository.findByVerificationStatus(BusinessProfile.VerificationStatus.VERIFIED);
        List<BusinessProfile> pendingProfiles = businessProfileRepository.findByVerificationStatus(BusinessProfile.VerificationStatus.PENDING);

        // then
        assertThat(verifiedProfiles).hasSize(1);
        assertThat(verifiedProfiles.get(0).getBusinessName()).isEqualTo("Verified Business");
        
        assertThat(pendingProfiles).hasSize(1);
        assertThat(pendingProfiles.get(0).getBusinessName()).isEqualTo("Pending Business");
    }

    @Test
    @DisplayName("Should search business profiles by name")
    void shouldSearchBusinessProfilesByName() {
        // given
        BusinessProfile profile1 = BusinessProfile.builder()
                .userId(101L)
                .businessName("Tech Solutions")
                .verificationStatus(BusinessProfile.VerificationStatus.VERIFIED)
                .active(true)
                .build();
        
        BusinessProfile profile2 = BusinessProfile.builder()
                .userId(102L)
                .businessName("Food Services")
                .verificationStatus(BusinessProfile.VerificationStatus.VERIFIED)
                .active(true)
                .build();
        
        entityManager.persist(profile1);
        entityManager.persist(profile2);
        entityManager.flush();

        // when
        List<BusinessProfile> techProfiles = businessProfileRepository.searchByBusinessName("Tech");
        List<BusinessProfile> foodProfiles = businessProfileRepository.searchByBusinessName("Food");

        // then
        assertThat(techProfiles).hasSize(1);
        assertThat(techProfiles.get(0).getBusinessName()).isEqualTo("Tech Solutions");
        
        assertThat(foodProfiles).hasSize(1);
        assertThat(foodProfiles.get(0).getBusinessName()).isEqualTo("Food Services");
    }
}