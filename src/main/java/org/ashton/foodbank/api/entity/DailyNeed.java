package org.ashton.foodbank.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.ashton.foodbank.api.entity.interfaces.HasId;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Daily donations. */
public class DailyNeed extends EntityMeta implements HasId {
  private static final String DAILY_NEED_SEQ = "DAILY_NEED_SEQ";
  private static final int DAILY_NEED_SEQ_VALUE = 80001;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DAILY_NEED_SEQ)
  @SequenceGenerator(
      name = DAILY_NEED_SEQ,
      sequenceName = DAILY_NEED_SEQ,
      initialValue = DAILY_NEED_SEQ_VALUE,
      allocationSize = 10)
  @Column(nullable = false, updatable = false)
  private Long id;

  @Column(name = "date", nullable = false)
  private String date;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private NeedStatus status = NeedStatus.Active;

  @ManyToOne
  @JoinColumn(name = "food_pantry_id", foreignKey = @ForeignKey(name = "FK_daily_need_to_pantry"))
  private FoodPantry foodPantry;

  @ElementCollection
  @CollectionTable(
      name = "daily_need_items",
      joinColumns = {@JoinColumn(name = "daily_need_id", referencedColumnName = "id")})
  @MapKeyColumn(name = "food_category")
  @Column(name = "quantity")
  private Map<String, Integer> needItems;

  @JsonIgnoreProperties("dailyNeed")
  @OneToMany(mappedBy = "dailyNeed", fetch = FetchType.EAGER)
  private Set<DailyDonation> dailyDonations = new HashSet<>();

  @Transient private Map<String, Integer> shortages = new HashMap<>();

  @Override
  public Long getId() {
    return id;
  }

  public String getDate() {
    return date;
  }

  public DailyNeed setDate(String date) {
    this.date = date;
    return this;
  }

  public NeedStatus getStatus() {
    return status;
  }

  public DailyNeed setStatus(NeedStatus status) {
    this.status = status;
    return this;
  }

  public FoodPantry getFoodPantry() {
    return foodPantry;
  }

  public DailyNeed setFoodPantry(FoodPantry foodPantry) {
    this.foodPantry = foodPantry;
    return this;
  }

  public Map<String, Integer> getNeedItems() {
    return needItems;
  }

  public Set<DailyDonation> getDailyDonations() {
    return dailyDonations;
  }

  public DailyNeed updateShortages() {
    shortages.clear();
    shortages.putAll(needItems);
    for (DailyDonation donation : dailyDonations) {
      donation
          .getDonationItems()
          .entrySet()
          .forEach(
              e -> {
                shortages.put(
                    e.getKey(),
                    shortages.containsKey(e.getKey())
                        ? shortages.get(e.getKey()) - e.getValue()
                        : -e.getValue());
              });
    }
    return this;
  }
}
