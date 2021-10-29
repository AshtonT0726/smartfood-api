package org.ashton.foodbank.api.entity;

public enum DonationStatus {
  // Scheduled to be delivered to a food pantry.
  Scheduled,

  // Delivery was completed to the scheduled food pantry.
  Completed,

  // Delivery was rescheduled to be delivered to a different pantry
  Rescheduled,

  // Delivery was completed to the rescheduled food pantry.
  CompletedWithReschedule,

  // Delivery was not committed to any food pantry.
  Unassigned,

  // Delivery was cancelled.
  Cancelled
}
