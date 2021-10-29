package org.ashton.foodbank.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.ashton.foodbank.api.entity.interfaces.HasId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/** Food can be re-delivered to other food pantries within a group. */
public class FoodPantryGroup extends EntityMeta implements HasId {
  private static final String FOOD_PANTRY_GROUP_SEQ = "FOOD_PANTRY_GROUP_SEQ";
  private static final int FOOD_PANTRY_GROUP_SEQ_VALUE = 15001;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FOOD_PANTRY_GROUP_SEQ)
  @SequenceGenerator(
      name = FOOD_PANTRY_GROUP_SEQ,
      sequenceName = FOOD_PANTRY_GROUP_SEQ,
      initialValue = FOOD_PANTRY_GROUP_SEQ_VALUE,
      allocationSize = 10)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @JsonIgnoreProperties("group")
  @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
  private Set<FoodPantry> pantries = new HashSet<>();

  @Override
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public FoodPantryGroup setName(String name) {
    this.name = name;
    return this;
  }

  public Set<FoodPantry> getPantries() {
    return pantries;
  }

  public FoodPantryGroup setPantries(Set<FoodPantry> pantries) {
    this.pantries = pantries;
    return this;
  }
}
