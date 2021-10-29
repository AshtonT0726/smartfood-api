package org.ashton.foodbank.api.entity;

import com.google.common.base.Preconditions;
import org.ashton.foodbank.api.entity.interfaces.HasId;

import javax.persistence.*;

/** Represents a volunteer. */
public class Volunteer extends EntityMeta implements HasId {
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

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "profile", nullable = false, length = 4096)
  private String profile;

  @Column(name = "phone", nullable = false, length = 12)
  private String phone;

  @Column(name = "email", nullable = false, length = 128)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private VolunteerStatus status = VolunteerStatus.Active;

  @Override
  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public Volunteer setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public String getLastName() {
    return lastName;
  }

  public Volunteer setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public String getProfile() {
    return profile;
  }

  public Volunteer setProfile(String profile) {
    this.profile = profile;
    return this;
  }

  public String getPhone() {
    return phone;
  }

  public Volunteer setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public Volunteer setEmail(String email) {
    this.email = email;
    return this;
  }

  public VolunteerStatus getStatus() {
    return status;
  }

  public Volunteer setStatus(VolunteerStatus status) {
    this.status = status;
    return this;
  }

  public void validate() {
    Preconditions.checkArgument(getEmail() != null, "email cannot be null.");
    Preconditions.checkArgument(getPhone() != null, "phone cannot be null.");
    Preconditions.checkArgument(getFirstName() != null, "firstName cannot be null.");
    Preconditions.checkArgument(getLastName() != null, "lastName cannot be null.");
  }
}
