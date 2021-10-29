package org.ashton.foodbank.api.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodNeedScheduleRepository extends JpaRepository<FoodNeedSchedule, Long> {}
