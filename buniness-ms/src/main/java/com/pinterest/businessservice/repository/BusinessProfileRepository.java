package com.pinterest.businessservice.repository;

import com.pinterest.businessservice.model.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {
    
    Optional<BusinessProfile> findByUserId(Long userId);
    
    List<BusinessProfile> findByActive(boolean active);
    
    @Query("SELECT b FROM BusinessProfile b WHERE b.verificationStatus = :status")
    List<BusinessProfile> findByVerificationStatus(BusinessProfile.VerificationStatus status);
    
    @Query("SELECT b FROM BusinessProfile b WHERE LOWER(b.businessName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BusinessProfile> searchByBusinessName(String keyword);
    
    @Query("SELECT b FROM BusinessProfile b WHERE b.category = :category AND b.active = true")
    List<BusinessProfile> findByCategory(BusinessProfile.BusinessCategory category);
}