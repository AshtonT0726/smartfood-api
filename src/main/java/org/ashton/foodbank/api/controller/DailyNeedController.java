package org.ashton.foodbank.api.controller;

import com.google.common.base.Preconditions;
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
public class DailyNeedController extends CrudController<DailyNeed> {
  private static final Logger LOG = LoggerFactory.getLogger(DailyNeedController.class);

  private final DailyNeedRepository dailyNeedRepository;
  private final DailyDonationRepository dailyDonationRepository;
  private final FoodPantryRepository foodPantryRepository;

  @Autowired
  public DailyNeedController(
      DailyNeedRepository dailyNeedRepository,
      DailyDonationRepository dailyDonationRepository,
      FoodPantryRepository foodPantryRepository) {
    super(dailyNeedRepository);
    this.dailyNeedRepository = dailyNeedRepository;
    this.dailyDonationRepository = dailyDonationRepository;
    this.foodPantryRepository = foodPantryRepository;
  }

  @GetMapping("/daily-needs/{id}")
  public ResponseEntity<Object> get(@PathVariable long id) {
    LOG.info("Get DailyNeed: {}", id);
    return ResponseEntity.ok(findEntity(id).updateShortages());
  }

  @PostMapping("/daily-needs")
  public ResponseEntity<Object> create(@RequestBody DailyNeed dailyNeed) {
    validateEntityForCreate(dailyNeed);
    Preconditions.checkArgument(
        dailyNeed.getDailyDonations().isEmpty(),
        "DailyDonations cannot be passed in during creation.");
    Preconditions.checkArgument(
        dailyNeed.getFoodPantry() == null || dailyNeed.getFoodPantry().getId() == null,
        "DailyNeed has to have a FoodPantry.");
    Preconditions.checkArgument(
        !dailyNeed.getNeedItems().isEmpty(), "DailyNeed has to have at least one items.");

    FoodPantry foodPantry =
        findEntity(foodPantryRepository, dailyNeed.getFoodPantry().getId(), "FoodPantry");
    dailyNeed.setFoodPantry(foodPantry);
    dailyNeed.setStatus(NeedStatus.Active);
    return ResponseEntity.status(HttpStatus.CREATED).body(dailyNeed.updateShortages());
  }

  /** This method only supporting updates to the needed Items. */
  @PutMapping("/daily-needs/{id}")
  public ResponseEntity<Object> update(@RequestBody DailyNeed dailyNeed, @PathVariable long id) {
    Preconditions.checkArgument(
        !dailyNeed.getNeedItems().isEmpty(), "DailyNeed has to have at least one items.");

    DailyNeed existed = findEntity(id);
    validateEntityForUpdate(dailyNeed, existed);

    existed.getNeedItems().clear();
    existed.getNeedItems().putAll(dailyNeed.getNeedItems());
    dailyNeedRepository.save(existed);

    return ResponseEntity.status(HttpStatus.OK).body(dailyNeed.updateShortages());
  }

  /**
   * This method cancels the DailyNeed. It will not be deleted, but rather marked as Cancelled
   * status.
   */
  @DeleteMapping("/daily-needs/{id}")
  public ResponseEntity<Object> delete(@PathVariable long id) {
    DailyNeed dailyNeed = findEntity(id);
    dailyNeed.setStatus(NeedStatus.Cancelled);
    dailyNeedRepository.save(dailyNeed);
    return ResponseEntity.status(HttpStatus.OK).body(dailyNeed);
  }

  @Override
  protected String getEntityName() {
    return "DailyNeed";
  }
}
