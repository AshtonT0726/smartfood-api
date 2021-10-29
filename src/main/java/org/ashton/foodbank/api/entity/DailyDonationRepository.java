package org.ashton.foodbank.api.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyDonationRepository extends JpaRepository<DailyDonation, Long> {}