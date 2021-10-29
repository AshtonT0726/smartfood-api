package org.ashton.foodbank.api.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodPantryRepository extends JpaRepository<FoodPantry, Long> {}
