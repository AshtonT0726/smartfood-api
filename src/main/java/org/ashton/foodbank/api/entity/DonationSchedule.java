package org.ashton.foodbank.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.ashton.foodbank.api.entity.interfaces.HasId;

import javax.persistence.*;
import java.util.Map;

/**
 * Donation schedule for every day. This is a general donation estimate from the food donor. The
 * donation quantities might be different on each day.
 */
public class DonationSchedule extends EntityMeta implements HasId {
  private static final String DONATION_SCHEDULE_SEQ = "DONATION_SCHEDULE_SEQ";
  private static final int DONATION_SCHEDULE_SEQ_VALUE = 30001;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DONATION_SCHEDULE_SEQ)
  @SequenceGenerator(
      name = DONATION_SCHEDULE_SEQ,
      sequenceName = DONATION_SCHEDULE_SEQ,
      initialValue = DONATION_SCHEDULE_SEQ_VALUE,
      allocationSize = 10)
  @Column(nullable = false, updatable = false)
  private Long id;

  @JsonIgnoreProperties("donationSchedules")
  @OneToOne
  @JoinColumn(name = "food_donor_id", foreignKey = @ForeignKey(name = "FK_schedule_to_donor"))
  private FoodDonor foodDonor;

  @ElementCollection
  @CollectionTable(
      name = "schedule_donation_items",
      joinColumns = {@JoinColumn(name = "schedule_id", referencedColumnName = "id")})
  @MapKeyColumn(name = "food_category")
  @Column(name = "quantity")
  private Map<String, Integer> donationItems;

  @Column(name = "ready_time", nullable = false)
  private String readyTime;

  @Override
  public Long getId() {
    return id;
  }

  public FoodDonor getFoodDonor() {
    return foodDonor;
  }

  public DonationSchedule setFoodDonor(FoodDonor foodDonor) {
    this.foodDonor = foodDonor;
    return this;
  }

  public Map<String, Integer> getDonationItems() {
    return donationItems;
  }

  public DonationSchedule setDonationItems(Map<String, Integer> donationItems) {
    this.donationItems = donationItems;
    return this;
  }

  public String getReadyTime() {
    return readyTime;
  }

  public DonationSchedule setReadyTime(String readyTime) {
    this.readyTime = readyTime;
    return this;
  }
}
