package org.ashton.foodbank.api.controller;

import com.google.common.base.Preconditions;
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

/** Controller for DonationSchedule. */
@RestController
@RequestMapping(
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE})
@Transactional
public class DonationScheduleController extends CrudController<DonationSchedule> {
  private static final Logger LOG = LoggerFactory.getLogger(DonationScheduleController.class);

  private final FoodDonorRepository foodDonorRepository;
  private final DonationScheduleRepository donationScheduleRepository;

  @Autowired
  public DonationScheduleController(
      DailyDonationRepository dailyDonationRepository,
      DailyNeedRepository dailyNeedRepository,
      VolunteerRepository volunteerRepository) {
    super(dailyDonationRepository);
    this.dailyDonationRepository = dailyDonationRepository;
    this.dailyNeedRepository = dailyNeedRepository;
    this.volunteerRepository = volunteerRepository;
  }

  @GetMapping("/daily-donations/{id}")
  public ResponseEntity<Object> get(@PathVariable long id) {
    return ResponseEntity.ok(findEntity(id));
  }

  @PostMapping("/daily-donations")
  public ResponseEntity<Object> create(@RequestBody DailyDonation dailyDonation) {
    validateEntityForCreate(dailyDonation);
    Preconditions.checkArgument(
        dailyDonation.getVolunteer() == null, "Volunteer cannot be passed in during creation.");
    Preconditions.checkArgument(
        dailyDonation.getDailyNeed() == null, "DailyNeed cannot be passed in during creation.");
    Preconditions.checkArgument(
        !dailyDonation.getDonationItems().isEmpty(),
        "DailyDonation has to have at least one items.");
    dailyDonation.setStatus(DonationStatus.Unassigned);
    dailyDonation = dailyDonationRepository.save(dailyDonation);
    return ResponseEntity.status(HttpStatus.CREATED).body(dailyDonation);
  }

  @PutMapping("/daily-donations/{id}")
  public ResponseEntity<Object> update(
      @PathVariable long id, @RequestBody DailyDonation dailyDonation) {
    Preconditions.checkArgument(
        !dailyDonation.getDonationItems().isEmpty(),
        "DailyDonation has to have at least one items.");

    DailyDonation existed = findEntity(id);
    validateEntityForUpdate(dailyDonation, existed);

    // Handle Volunteer
    if (dailyDonation.getVolunteer() != null) {
      if (dailyDonation.getVolunteer().getId() == null) {
        throw new BadRequestException("Volunteer must have an Id");
      } else {
        Volunteer volunteer =
            findEntity(volunteerRepository, dailyDonation.getVolunteer().getId(), "Volunteer");
        dailyDonation.setVolunteer(volunteer);
      }
    }

    // Handle DailyNeed
    if (dailyDonation.getDailyNeed() != null) {
      if (dailyDonation.getDailyNeed().getId() == null) {
        throw new BadRequestException("DailyNeed must have an Id");
      } else {
        DailyNeed dailyNeed =
            findEntity(dailyNeedRepository, dailyDonation.getDailyNeed().getId(), "DailyNeed");
        dailyDonation.setDailyNeed(dailyNeed);
      }
    }

    dailyDonation.copyCreatedInfo(existed);
    dailyDonation = dailyDonationRepository.save(dailyDonation);
    return ResponseEntity.status(HttpStatus.OK).body(dailyDonation);
  }

  /**
   * Complete a DailyDonation to a DailyNeed. If the DailyDonation is not committed to a DailyNeed,
   * an error message is returned. if the DailyDonation doesn't have a Volunteer assigned, an error
   * message is returned.
   */
  @PostMapping("/daily-donations/{id}/complete")
  public ResponseEntity<Object> complete(@PathVariable long id) {
    LOG.info("Complete donation {}", id);

    DailyDonation dailyDonation = findEntity(id);

    if (dailyDonation.getStatus() == DonationStatus.Completed
        || dailyDonation.getStatus() == DonationStatus.Cancelled) {
      throw new BadRequestException(
          "DailyDonation " + id + " is already " + dailyDonation.getStatus());
    }
    if (dailyDonation.getDailyNeed() == null) {
      throw new BadRequestException("DailyDonation " + id + " is not assigned to a DailyNeed.");
    }
    if (dailyDonation.getVolunteer() == null) {
      throw new BadRequestException("DailyDonation " + id + " is not assigned to a Volunteer.");
    }

    dailyDonation.complete();
    dailyDonationRepository.save(dailyDonation);

    return ResponseEntity.ok(dailyDonation);
  }

  /** Cancels a DailyDonation. It will remove the DailyNeed if there is one. */
  @PostMapping("/daily-donations/{id}/cancel")
  public ResponseEntity<Object> cancel(@PathVariable long id) {
    LOG.info("Cancel donation {}", id);

    DailyDonation dailyDonation = findEntity(id);

    if (dailyDonation.getStatus() == DonationStatus.Completed
        || dailyDonation.getStatus() == DonationStatus.Cancelled) {
      throw new BadRequestException(
          "DailyDonation " + id + " is already " + dailyDonation.getStatus());
    }

    dailyDonation.setStatus(DonationStatus.Cancelled);
    if (dailyDonation.getDailyNeed() != null) {
      dailyDonation.getDailyNeed().getDailyDonations().remove(dailyDonation);
      dailyDonation.setDailyNeed(null);
    }
    dailyNeedRepository.save(dailyDonation.getDailyNeed());
    dailyDonationRepository.save(dailyDonation);

    return ResponseEntity.ok(dailyDonation);
  }

  /**
   * Commit a DailyDonation to a DailyNeed. If the DailyDonation has already committed to a
   * different DailyNeed, that commitment is automatically cancelled.
   */
  @PostMapping("/daily-donations/{dailyDonationId}/commit/{dailyNeedId}")
  public ResponseEntity<Object> commitDonation(
      @PathVariable long dailyDonationId, @PathVariable long dailyNeedId) {
    LOG.info("Commit DailyDonation {} to DailyNeed {}", dailyDonationId, dailyNeedId);

    DailyDonation dailyDonation = findEntity(dailyDonationId);
    if (dailyDonation.getStatus() == DonationStatus.Completed
        || dailyDonation.getStatus() == DonationStatus.Cancelled) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("DailyDonation " + dailyDonationId + " is already " + dailyDonation.getStatus());
    }

    DailyNeed dailyNeed = findEntity(dailyNeedRepository, dailyNeedId, "DailyNeed");
    if (dailyDonation.getDailyNeed() != null) {
      if (dailyDonation.getDailyNeed().getId().equals(dailyNeed.getId())) {
        // No-op
        return ResponseEntity.ok(dailyNeed);
      } else {
        dailyDonation.getDailyNeed().getDailyDonations().remove(dailyDonation);
        dailyDonation.setDailyNeed(null);
        dailyDonation.setStatus(DonationStatus.Rescheduled);
      }
    } else {
      dailyDonation.setStatus(DonationStatus.Scheduled);
    }

    dailyDonation.getDailyNeed().getDailyDonations().remove(dailyDonation);
    dailyDonation.setDailyNeed(null);
    dailyDonation.setStatus(DonationStatus.Unassigned);

    dailyNeedRepository.save(dailyNeed);
    dailyDonationRepository.save(dailyDonation);

    return ResponseEntity.ok(dailyDonation);
  }

  /**
   * Withdrawa DailyDonation to a DailyNeed. If the DailyDonation was not committed to the
   * DailyNeed, the request will fail.
   */
  @PostMapping("/daily-donations/{dailyDonationId}/withdraw/{dailyNeedId}")
  public ResponseEntity<Object> withdrawDonation(
      @PathVariable long dailyDonationId, @PathVariable long dailyNeedId) {
    LOG.info("Withdraw DailyDonation {} to DailyNeed {}", dailyDonationId, dailyNeedId);

    DailyDonation dailyDonation = findEntity(dailyDonationId);
    if (dailyDonation.getStatus() == DonationStatus.Completed
        || dailyDonation.getStatus() == DonationStatus.Cancelled) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("DailyDonation " + dailyDonationId + " is already " + dailyDonation.getStatus());
    }

    DailyNeed dailyNeed = findEntity(dailyNeedRepository, dailyNeedId, "DailyNeed");
    if (dailyDonation.getDailyNeed() == null
        || dailyDonation.getDailyNeed().getId().equals(dailyNeedId)) {

      throw new BadRequestException(
          "DailyDonation " + dailyDonationId + " is not committed to DailyNeed " + dailyNeedId);
    }

    dailyNeed.getDailyDonations().add(dailyDonation);
    dailyDonation.setDailyNeed(dailyNeed);

    dailyNeedRepository.save(dailyNeed);
    dailyDonationRepository.save(dailyDonation);

    return ResponseEntity.ok(dailyDonation);
  }

  @Override
  protected String getEntityName() {
    return "DailyDonation";
  }
}
