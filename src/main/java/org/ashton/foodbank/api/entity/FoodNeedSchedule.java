package org.ashton.foodbank.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Map;

/**
 * Food need for a food pantry on daily basis. This is a general need estimate from the food pantry.
 * The food needs might be different on each day.
 */
public class FoodNeedSchedule extends EntityMeta {
  private static final String FOOD_NEED_SEQ = "FOOD_NEED_SEQ";
  private static final int FOOD_NEED_SEQ_VALUE = 60001;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FOOD_NEED_SEQ)
  @SequenceGenerator(
      name = FOOD_NEED_SEQ,
      sequenceName = FOOD_NEED_SEQ,
      initialValue = FOOD_NEED_SEQ_VALUE,
      allocationSize = 10)
  @Column(nullable = false, updatable = false)
  private Long id;

  @JsonIgnoreProperties("foodNeedSchedule")
  @OneToOne
  @JoinColumn(name = "food_pantry_id", foreignKey = @ForeignKey(name = "FK_need_to_pantry"))
  private FoodPantry foodPantry;

  @ElementCollection
  @CollectionTable(
      name = "food_need_items",
      joinColumns = {@JoinColumn(name = "need_id", referencedColumnName = "id")})
  @MapKeyColumn(name = "food_category")
  @Column(name = "quantity")
  private Map<String, Integer> needItems;
}
