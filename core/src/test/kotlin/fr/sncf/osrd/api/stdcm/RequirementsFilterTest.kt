package fr.sncf.osrd.api.stdcm

import com.google.common.collect.Range
import com.google.common.collect.TreeRangeSet
import fr.sncf.osrd.sim_infra.api.ZoneId
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RequirementsFilterTest {

    private val window =
        RequirementsFilter.WindowBounds(beginEpoch = 100.0, endEpoch = 200.0)

    @Test
    fun zoneUseIntersectsWindowWhenRangeCrossesBoundary() {
        val range = Range.closedOpen(150.0, 250.0)
        assertTrue(RequirementsFilter.zoneUseIntersectsWindow(range, window))
    }

    @Test
    fun zoneUseIgnoresRangeEndingExactlyAtWindowStart() {
        val range = Range.closedOpen(50.0, 100.0)
        assertFalse(RequirementsFilter.zoneUseIntersectsWindow(range, window))
    }

    @Test
    fun detailedRequirementInsideWindowRequiresFullContainment() {
        assertTrue(RequirementsFilter.detailedRequirementInsideWindow(110.0, 190.0, window))
        assertTrue(RequirementsFilter.detailedRequirementInsideWindow(100.0, 200.0, window))
    }

    @Test
    fun detailedRequirementRejectsPartialOverlap() {
        assertFalse(RequirementsFilter.detailedRequirementInsideWindow(90.0, 150.0, window))
        assertFalse(RequirementsFilter.detailedRequirementInsideWindow(150.0, 210.0, window))
    }

    @Test
    fun filterZoneUsesShiftsMatchingRanges() {
        val zone = ZoneId(1)
        val rangeSet = TreeRangeSet.create<Double>()
        rangeSet.add(Range.closedOpen(120.0, 180.0))
        val filtered =
            RequirementsFilter.filterZoneUses(mapOf(zone to rangeSet), window).rangesByZone
        val shifted = filtered[zone]!!.asRanges().single()
        assertTrue(shifted.lowerEndpoint() == 20.0)
        assertTrue(shifted.upperEndpoint() == 80.0)
    }
}
