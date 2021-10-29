package org.ashton.foodbank.api.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/** Food Category. */
public enum FoodCategory {
  SOUP("Soup"),
  BREAD("Bread"),
  PASTRY("pastry"),
  BAKERY("Bakery Goods"),
  SANDWICH("Sandwich"),
  FRUIT("Fresh Fruite"),
  CANNED_FOOD("Canned food"),
  HOT_DISHES("Hot Dishes"),
  OTHERS("Others");

  private final String description;

  FoodCategory(String description) {
    this.description = description;
  }

  @JsonValue
  public String getDescription() {
    return description;
  }
}
