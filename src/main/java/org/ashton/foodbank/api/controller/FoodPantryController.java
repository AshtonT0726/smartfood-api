package org.ashton.foodbank.api.controller;

import org.ashton.foodbank.api.entity.FoodPantry;
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
public class FoodPantryController extends CrudController<FoodPantry> {
  private static final Logger LOG = LoggerFactory.getLogger(FoodPantryController.class);

  private final FoodPantryRepository foodPantryRepository;

  @Autowired
  public FoodPantryController(FoodPantryRepository foodPantryRepository) {
    super(foodPantryRepository);
    this.foodPantryRepository = foodPantryRepository;
  }

  @GetMapping("/food-pantries/{id}")
  public ResponseEntity<Object> get(@PathVariable long id) {
    LOG.info("Get FoodPantry: {}", id);
    return ResponseEntity.ok(findEntity(id));
  }

  @PostMapping("/food-pantries")
  public ResponseEntity<Object> create(@RequestBody FoodPantry foodPantry) {
    foodPantry.validate();

    validateEntityForCreate(foodPantry);

    foodPantry = foodPantryRepository.save(foodPantry);
    return ResponseEntity.status(HttpStatus.CREATED).body(foodPantry);
  }

  @PutMapping("/food-pantries/{id}")
  public ResponseEntity<Object> update(@PathVariable long id, @RequestBody FoodPantry foodPantry) {
    foodPantry.validate();
    FoodPantry existed = findEntity(id);

    validateEntityForUpdate(foodPantry, existed);

    existed.setEmail(foodPantry.getEmail());
    existed.setPhone(foodPantry.getPhone());
    existed.setAddress(foodPantry.getAddress());
    existed.setDescription(foodPantry.getDescription());
    existed.setWebsite(foodPantry.getWebsite());
    existed.setName(foodPantry.getName());
    foodPantryRepository.save(existed);

    return ResponseEntity.status(HttpStatus.OK).body(existed);
  }

  @Override
  protected String getEntityName() {
    return "Volunteer";
  }
}
