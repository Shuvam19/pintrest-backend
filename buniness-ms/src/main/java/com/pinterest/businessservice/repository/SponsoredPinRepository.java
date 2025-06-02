package com.pinterest.businessservice.repository;

import com.pinterest.businessservice.model.SponsoredPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SponsoredPinRepository extends JpaRepository<SponsoredPin, Long> {
    
    List<SponsoredPin> findByBusinessProfileId(Long businessProfileId);
    
    List<SponsoredPin> findByPinId(Long pinId);
    
    List<SponsoredPin> findByCampaignId(Long campaignId);
    
    List<SponsoredPin> findByStatus(SponsoredPin.SponsoredStatus status);
    
    @Query("SELECT sp FROM SponsoredPin sp WHERE sp.status = 'ACTIVE' AND sp.startDate <= :now AND (sp.endDate IS NULL OR sp.endDate >= :now)")
    List<SponsoredPin> findActiveSponsoredPins(LocalDateTime now);
    
    @Query("SELECT sp FROM SponsoredPin sp WHERE sp.businessProfileId = :businessProfileId AND sp.status = :status")
    List<SponsoredPin> findByBusinessProfileIdAndStatus(Long businessProfileId, SponsoredPin.SponsoredStatus status);
    
    @Query("SELECT sp FROM SponsoredPin sp WHERE sp.campaignId = :campaignId AND sp.status = :status")
    List<SponsoredPin> findByCampaignIdAndStatus(Long campaignId, SponsoredPin.SponsoredStatus status);
}