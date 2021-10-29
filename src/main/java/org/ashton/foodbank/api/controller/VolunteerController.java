package org.ashton.foodbank.api.controller;

import org.ashton.foodbank.api.controller.exception.BadRequestException;
import org.ashton.foodbank.api.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE})
@Transactional
public class VolunteerController extends CrudController<Volunteer> {
  private static final Logger LOG = LoggerFactory.getLogger(VolunteerController.class);

  private final VolunteerRepository volunteerRepository;
  private final DailyDonationRepository dailyDonationRepository;

  @Autowired
  public VolunteerController(
      VolunteerRepository volunteerRepository, DailyDonationRepository dailyDonationRepository) {
    super(volunteerRepository);
    this.volunteerRepository = volunteerRepository;
    this.dailyDonationRepository = dailyDonationRepository;
  }

  @GetMapping("/volunteers/{id}")
  public ResponseEntity<Object> get(@PathVariable long id) {
    LOG.info("Get Volunteer: {}", id);
    return ResponseEntity.ok(findEntity(id));
  }

  @PostMapping("/volunteers")
  public ResponseEntity<Object> create(@RequestBody Volunteer volunteer) {
    volunteer.validate();
    volunteer.setStatus(VolunteerStatus.Active);

    validateEntityForCreate(volunteer);

    volunteer = volunteerRepository.save(volunteer);
    return ResponseEntity.status(HttpStatus.CREATED).body(volunteer);
  }

  @PutMapping("/volunteers/{id}")
  public ResponseEntity<Object> update(@PathVariable long id, @RequestBody Volunteer volunteer) {
    volunteer.validate();
    Volunteer existed = findEntity(id);
    if (existed.getStatus() == VolunteerStatus.Inactive) {
      throw new BadRequestException("Cannot update an inactive volunteer.");
    }
    validateEntityForUpdate(volunteer, existed);

    existed.setEmail(volunteer.getEmail());
    existed.setPhone(volunteer.getPhone());
    existed.setFirstName(volunteer.getFirstName());
    existed.setLastName(volunteer.getLastName());
    existed.setPhone(volunteer.getProfile());
    volunteerRepository.save(existed);

    return ResponseEntity.status(HttpStatus.OK).body(existed);
  }

  @DeleteMapping("/volunteers/{id}")
  public ResponseEntity<Object> delete(@PathVariable long id) {
    Volunteer existed = findEntity(id);
    existed.setStatus(VolunteerStatus.Inactive);
    volunteerRepository.save(existed);
    return ResponseEntity.status(HttpStatus.OK).body(existed);
  }

  @GetMapping("/volunteers/{id}/signup/{dailyDonationId}")
  public ResponseEntity<Object> signup(@PathVariable long id, @PathVariable long dailyDonationId) {
    LOG.info("Sign up Volunteer {} with Daily Donation {}", id, dailyDonationId);

    Volunteer volunteer = findEntity(id);
    if (volunteer.getStatus() == VolunteerStatus.Inactive) {
      throw new BadRequestException("Cannot sign up an inactive volunteer.");
    }

    DailyDonation dailyDonation =
        findEntity(dailyDonationRepository, dailyDonationId, "DailyDonation");
    if (dailyDonation.getStatus() != DonationStatus.Unassigned
        && dailyDonation.getStatus() != DonationStatus.Scheduled
        && dailyDonation.getStatus() != DonationStatus.Rescheduled) {
      throw new BadRequestException("Cannot sign up an donation that has completed or cancelled.");
    }

    dailyDonation.setVolunteer(volunteer);
    dailyDonationRepository.save(dailyDonation);

    return ResponseEntity.ok(volunteer);
  }

  @DeleteMapping("/volunteers/{id}/signup/{dailyDonationId}")
  public ResponseEntity<Object> withdraw(
      @PathVariable long id, @PathVariable long dailyDonationId) {
    LOG.info("Sign up Volunteer {} with Daily Donation {}", id, dailyDonationId);

    Volunteer volunteer = findEntity(id);

    DailyDonation dailyDonation =
        findEntity(dailyDonationRepository, dailyDonationId, "DailyDonation");

    if (dailyDonation.getVolunteer() == null) {
      return ResponseEntity.ok().build();
    } else if (dailyDonation.getVolunteer().getId().longValue() != volunteer.getId().longValue()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Volunteer " + id + " is not signed up with DailyDonation " + dailyDonationId);
    } else {
      dailyDonation.setVolunteer(null);
      dailyDonationRepository.save(dailyDonation);
      return ResponseEntity.ok(volunteer);
    }
  }

  @Override
  protected String getEntityName() {
    return "Volunteer";
  }
}
