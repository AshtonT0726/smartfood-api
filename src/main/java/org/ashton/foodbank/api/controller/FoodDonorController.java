package org.ashton.foodbank.api.controller;

import org.ashton.foodbank.api.entity.FoodDonor;
import org.ashton.foodbank.api.entity.FoodDonorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for FoodDonor. */
@RestController
@RequestMapping(
    consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE})
@Transactional
public class FoodDonorController extends CrudController<FoodDonor> {
  private static final Logger LOG = LoggerFactory.getLogger(FoodDonorController.class);

  private final FoodDonorRepository foodDonorRepository;

  @Autowired
  public FoodDonorController(FoodDonorRepository foodDonorRepository) {
    super(foodDonorRepository);
    this.foodDonorRepository = foodDonorRepository;
  }

  @Override
  protected String getEntityName() {
    return "FoodDonor";
  }
}
