package org.ashton.foodbank.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.ashton.foodbank.api.entity.interfaces.HasId;

import javax.persistence.*;
import java.util.Map;

/** Daily donations. */
public class DailyDonation extends EntityMeta implements HasId {
  private static final String DONATION_SEQ = "DONATION_SEQ";
  private static final int DONATION_SEQ_VALUE = 40001;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DONATION_SEQ)
  @SequenceGenerator(
      name = DONATION_SEQ,
      sequenceName = DONATION_SEQ,
      initialValue = DONATION_SEQ_VALUE,
      allocationSize = 10)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(name = "date", nullable = false)
  private String date;

  @Column(name = "ready_time", nullable = false)
  private String readyTime;

  @JsonIgnoreProperties("donationSchedule")
  @ManyToOne
  @JoinColumn(name = "food_donor_id", foreignKey = @ForeignKey(name = "FK_daily_donation_to_donor"))
  private FoodDonor foodDonor;

  @ElementCollection
  @CollectionTable(
      name = "daily_donation_items",
      joinColumns = {@JoinColumn(name = "daily_donation_id", referencedColumnName = "id")})
  @MapKeyColumn(name = "food_category")
  @Column(name = "quantity")
  private Map<String, Integer> donationItems;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private DonationStatus status = DonationStatus.Unassigned;

  // The volunteer who delivered the food.
  @ManyToOne
  @JoinColumn(
      name = "volunteer_id",
      foreignKey = @ForeignKey(name = "FK_daily_donation_to_volunteer"))
  private Volunteer volunteer;

  // The daily need from a food pantry.
  @ManyToOne
  @JoinColumn(name = "daily_need_id", foreignKey = @ForeignKey(name = "FK_daily_donation_to_need"))
  private DailyNeed dailyNeed;

  // The daily need from a food pantry.
  @ManyToOne
  @JoinColumn(
      name = "rescheduled_daily_need_id",
      foreignKey = @ForeignKey(name = "FK_daily_donation_to_rescheduled_need"))
  private DailyNeed rescheduledDailyNeed;

  @Override
  public Long getId() {
    return id;
  }

  public String getDate() {
    return date;
  }

  public DailyDonation setDate(String date) {
    this.date = date;
    return this;
  }

  public String getReadyTime() {
    return readyTime;
  }

  public DailyDonation setReadyTime(String readyTime) {
    this.readyTime = readyTime;
    return this;
  }

  public FoodDonor getFoodDonor() {
    return foodDonor;
  }

  public DailyDonation setFoodDonor(FoodDonor foodDonor) {
    this.foodDonor = foodDonor;
    return this;
  }

  public Map<String, Integer> getDonationItems() {
    return donationItems;
  }

  public DailyDonation setDonationItems(Map<String, Integer> donationItems) {
    this.donationItems = donationItems;
    return this;
  }

  public DonationStatus getStatus() {
    return status;
  }

  public DailyDonation setStatus(DonationStatus status) {
    this.status = status;
    return this;
  }

  public DailyDonation complete() {
    if (status == DonationStatus.Scheduled) {
      status = DonationStatus.Completed;
    } else if (status == DonationStatus.Rescheduled) {
      status = DonationStatus.CompletedWithReschedule;
    } else {
      throw new IllegalStateException("Cannot complete a donation in status of " + status);
    }
    return this;
  }

  public Volunteer getVolunteer() {
    return volunteer;
  }

  public DailyDonation setVolunteer(Volunteer volunteer) {
    this.volunteer = volunteer;
    return this;
  }

  public DailyNeed getDailyNeed() {
    return dailyNeed;
  }

  public DailyDonation setDailyNeed(DailyNeed dailyNeed) {
    this.dailyNeed = dailyNeed;
    return this;
  }

  public DailyNeed getRescheduledDailyNeed() {
    return rescheduledDailyNeed;
  }

  public DailyDonation setRescheduledDailyNeed(DailyNeed rescheduledDailyNeed) {
    this.rescheduledDailyNeed = rescheduledDailyNeed;
    return this;
  }
}
