package org.ashton.foodbank.api.controller;

import org.ashton.foodbank.api.controller.exception.BadRequestException;
import org.ashton.foodbank.api.entity.FoodPantry;
import org.ashton.foodbank.api.entity.FoodPantryGroup;
import org.ashton.foodbank.api.entity.FoodPantryGroupRepository;
import org.ashton.foodbank.api.entity.FoodPantryRepository;
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
public class FoodPantryGroupController extends CrudController<FoodPantryGroup> {
  private static final Logger LOG = LoggerFactory.getLogger(FoodPantryGroupController.class);

  private final FoodPantryGroupRepository groupRepository;
  private final FoodPantryRepository foodPantryRepository;

  @Autowired
  public FoodPantryGroupController(
      FoodPantryGroupRepository groupRepository, FoodPantryRepository foodPantryRepository) {
    super(groupRepository);
    this.groupRepository = groupRepository;
    this.foodPantryRepository = foodPantryRepository;
  }

  @GetMapping("/food-pantry-groups/{id}")
  public ResponseEntity<Object> get(@PathVariable long id) {
    LOG.info("Get FoodPantryGroup: {}", id);
    return ResponseEntity.ok(findEntity(id));
  }

  @PostMapping("/food-pantry-groups")
  public ResponseEntity<Object> create(@RequestBody FoodPantryGroup group) {
    validateEntityForCreate(group);

    group = groupRepository.save(group);
    return ResponseEntity.status(HttpStatus.CREATED).body(group);
  }

  @PostMapping("/food-pantry-groups/{id}/food-pantries/{pantryId}")
  public ResponseEntity<Object> addToGroup(@PathVariable long id, @PathVariable long pantryId) {
    LOG.info("Add Pantry {} to group {}", pantryId, id);

    FoodPantryGroup group = findEntity(id);
    FoodPantry pantry = findEntity(foodPantryRepository, pantryId, "FoodPantry");
    if (pantry.getGroup() != null && !pantry.getGroup().getId().equals(group.getId())) {
      pantry.getGroup().getPantries().remove(pantry);
      group.getPantries().add(pantry);
    }
    pantry.setGroup(group);
    group.getPantries().add(pantry);

    foodPantryRepository.save(pantry);
    groupRepository.save(group);

    return ResponseEntity.ok(group);
  }

  @DeleteMapping("/food-pantry-groups/{id}/food-pantries/{pantryId}")
  public ResponseEntity<Object> removeFromGroup(
      @PathVariable long id, @PathVariable long pantryId) {
    LOG.info("Remove Pantry {} from group {}", pantryId, id);

    FoodPantryGroup group = findEntity(id);
    FoodPantry pantry = findEntity(foodPantryRepository, pantryId, "FoodPantry");
    if (pantry.getGroup() == null) {
      return ResponseEntity.ok(group);
    } else if (!pantry.getGroup().getId().equals(group.getId())) {
      throw new BadRequestException("FoodPantry " + pantryId + " does not belong to Group " + id);
    } else {
      group.getPantries().remove(pantry);
      pantry.setGroup(null);

      foodPantryRepository.save(pantry);
      groupRepository.save(group);
      return ResponseEntity.ok(group);
    }
  }

  @Override
  protected String getEntityName() {
    return "Volunteer";
  }
}
