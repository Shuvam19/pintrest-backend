package com.pinterest.businessservice.repository;

import com.pinterest.businessservice.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    
    List<Campaign> findByBusinessProfileId(Long businessProfileId);
    
    List<Campaign> findByStatus(Campaign.CampaignStatus status);
    
    List<Campaign> findByObjective(Campaign.CampaignObjective objective);
    
    @Query("SELECT c FROM Campaign c WHERE c.businessProfileId = :businessProfileId AND c.status = :status")
    List<Campaign> findByBusinessProfileIdAndStatus(Long businessProfileId, Campaign.CampaignStatus status);
    
    @Query("SELECT c FROM Campaign c WHERE c.status = 'ACTIVE' AND c.startDate <= :now AND (c.endDate IS NULL OR c.endDate >= :now)")
    List<Campaign> findActiveCampaigns(LocalDateTime now);
    
    @Query("SELECT c FROM Campaign c WHERE c.status = 'SCHEDULED' AND c.startDate <= :now")
    List<Campaign> findCampaignsToActivate(LocalDateTime now);
    
    @Query("SELECT c FROM Campaign c WHERE c.status = 'ACTIVE' AND c.endDate <= :now")
    List<Campaign> findCampaignsToComplete(LocalDateTime now);
}