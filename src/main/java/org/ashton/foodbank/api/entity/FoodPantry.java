package org.ashton.foodbank.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Preconditions;
import org.ashton.foodbank.api.entity.interfaces.HasId;

import javax.persistence.*;
import java.util.Objects;

public class FoodPantry extends EntityMeta implements HasId {
  private static final String FOOD_BANK_SEQ = "FOOD_BANK_SEQ";
  private static final int FOOD_BANK_SEQ_VALUE = 10001;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FOOD_BANK_SEQ)
  @SequenceGenerator(
      name = FOOD_BANK_SEQ,
      sequenceName = FOOD_BANK_SEQ,
      initialValue = FOOD_BANK_SEQ_VALUE,
      allocationSize = 10)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false, length = 4096)
  private String description;

  @Column(name = "address", nullable = false, length = 4096)
  private String address;

  @Column(name = "website", nullable = false, length = 255)
  private String website;

  @Column(name = "phone", nullable = false, length = 12)
  private String phone;

  @Column(name = "email", nullable = false, length = 128)
  private String email;

  /** The scheduled food need for this pantry. */
  @JsonIgnoreProperties("foodPantry")
  @OneToOne(mappedBy = "foodPantry", targetEntity = FoodNeedSchedule.class, orphanRemoval = true)
  private FoodNeedSchedule foodNeedSchedule;

  @JsonIgnoreProperties("pantries")
  @ManyToOne
  private FoodPantryGroup group;

  @Override
  public Long getId() {
    return id;
  }

  public FoodPantry setId(Long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public FoodPantry setName(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public FoodPantry setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getAddress() {
    return address;
  }

  public FoodPantry setAddress(String address) {
    this.address = address;
    return this;
  }

  public String getWebsite() {
    return website;
  }

  public FoodPantry setWebsite(String website) {
    this.website = website;
    return this;
  }

  public String getPhone() {
    return phone;
  }

  public FoodPantry setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public FoodPantry setEmail(String email) {
    this.email = email;
    return this;
  }

  public FoodPantryGroup getGroup() {
    return group;
  }

  public FoodPantry setGroup(FoodPantryGroup group) {
    this.group = group;
    return this;
  }

  public FoodNeedSchedule getFoodNeedSchedule() {
    return foodNeedSchedule;
  }

  public void validate() {
    Preconditions.checkArgument(getEmail() != null, "email cannot be null.");
    Preconditions.checkArgument(getName() != null, "name cannot be null.");
    Preconditions.checkArgument(getPhone() != null, "phone cannot be null.");
    Preconditions.checkArgument(getAddress() != null, "address cannot be null.");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FoodPantry)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    FoodPantry foodBank = (FoodPantry) o;
    return getId().equals(foodBank.getId())
        && getName().equals(foodBank.getName())
        && getDescription().equals(foodBank.getDescription())
        && getAddress().equals(foodBank.getAddress())
        && getWebsite().equals(foodBank.getWebsite())
        && getPhone().equals(foodBank.getPhone())
        && getEmail().equals(foodBank.getEmail());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        getId(),
        getName(),
        getDescription(),
        getAddress(),
        getWebsite(),
        getPhone(),
        getEmail());
  }
}
