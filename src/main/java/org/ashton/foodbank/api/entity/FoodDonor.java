package org.ashton.foodbank.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.ashton.foodbank.api.entity.interfaces.HasId;

import javax.persistence.*;
import java.util.Objects;

/** A food donor donates food. */
public class FoodDonor extends EntityMeta implements HasId {
  private static final String FOOD_DONOR_SEQ = "FOOD_DONOR_SEQ";
  private static final int FOOD_DONOR_SEQ_VALUE = 20001;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FOOD_DONOR_SEQ)
  @SequenceGenerator(
      name = FOOD_DONOR_SEQ,
      sequenceName = FOOD_DONOR_SEQ,
      initialValue = FOOD_DONOR_SEQ_VALUE,
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

  /** The donation schedule associated with it. */
  @JsonIgnoreProperties("foodDonor")
  @OneToOne(mappedBy = "foodDonor", targetEntity = DonationSchedule.class, orphanRemoval = true)
  private DonationSchedule donationSchedule;

  @Override
  public Long getId() {
    return id;
  }

  public FoodDonor setId(Long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public FoodDonor setName(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public FoodDonor setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getAddress() {
    return address;
  }

  public FoodDonor setAddress(String address) {
    this.address = address;
    return this;
  }

  public String getWebsite() {
    return website;
  }

  public FoodDonor setWebsite(String website) {
    this.website = website;
    return this;
  }

  public String getPhone() {
    return phone;
  }

  public FoodDonor setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public FoodDonor setEmail(String email) {
    this.email = email;
    return this;
  }

  public DonationSchedule getDonationSchedule() {
    return donationSchedule;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FoodDonor)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    FoodDonor foodBank = (FoodDonor) o;
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
