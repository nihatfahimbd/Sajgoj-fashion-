package com.example

import com.example.data.model.OrderTrackingHelper
import com.example.data.model.OrderTrackingStage
import com.example.data.model.OrderTrackingSyncState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OrderTrackingStepperTest {

  @Test
  fun resolveProgress_mapsPipelineStepsAccurately() {
    val ordered = OrderTrackingHelper.resolveProgress("Pending")
    assertEquals(OrderTrackingStage.ORDERED, ordered.stage)
    assertEquals(0, ordered.currentStepIndex)
    assertFalse(ordered.isTerminalException)

    val confirmed = OrderTrackingHelper.resolveProgress("Confirmed")
    assertEquals(OrderTrackingStage.PROCESSING, confirmed.stage)
    assertEquals(1, confirmed.currentStepIndex)
    assertFalse(confirmed.isTerminalException)

    val processing = OrderTrackingHelper.resolveProgress("Processing")
    assertEquals(OrderTrackingStage.PROCESSING, processing.stage)
    assertEquals(1, processing.currentStepIndex)
    assertFalse(processing.isTerminalException)

    val toShip = OrderTrackingHelper.resolveProgress("To Ship")
    assertEquals(OrderTrackingStage.PROCESSING, toShip.stage)
    assertEquals(1, toShip.currentStepIndex)

    val shipped = OrderTrackingHelper.resolveProgress("Shipped")
    assertEquals(OrderTrackingStage.SHIPPED, shipped.stage)
    assertEquals(2, shipped.currentStepIndex)
    assertFalse(shipped.isTerminalException)

    val delivered = OrderTrackingHelper.resolveProgress("Delivered")
    assertEquals(OrderTrackingStage.DELIVERED, delivered.stage)
    assertEquals(3, delivered.currentStepIndex)
    assertFalse(delivered.isTerminalException)
  }

  @Test
  fun resolveProgress_handlesTerminalExceptionStates() {
    val cancelled = OrderTrackingHelper.resolveProgress("Cancelled")
    assertEquals(OrderTrackingStage.CANCELLED, cancelled.stage)
    assertTrue(cancelled.isTerminalException)

    val failed = OrderTrackingHelper.resolveProgress("Failed Delivered")
    assertEquals(OrderTrackingStage.FAILED, failed.stage)
    assertTrue(failed.isTerminalException)
  }

  @Test
  fun pipelineSteps_containsExpectedMilestonesAndSyncStateDefaults() {
    val titles = OrderTrackingHelper.pipelineSteps.map { it.titleEn }
    assertEquals(listOf("Ordered", "Processing", "Shipped", "Delivered"), titles)

    val defaultSync = OrderTrackingSyncState()
    assertFalse(defaultSync.isListening)
    assertFalse(defaultSync.isLiveServerSynced)
    assertTrue(defaultSync.isFromCache)
  }
}
