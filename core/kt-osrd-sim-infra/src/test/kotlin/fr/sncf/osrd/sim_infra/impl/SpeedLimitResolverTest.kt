package fr.sncf.osrd.sim_infra.impl

import fr.sncf.osrd.utils.units.Speed
import fr.sncf.osrd.utils.units.kilometersPerHour
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SpeedLimitResolverTest {

    private fun speedSection(vararg tagSpeeds: Pair<String, Speed>): SpeedSection {
        return SpeedSection(
            default = 120.kilometersPerHour,
            speedByTrainTag = tagSpeeds.toMap(),
            speedByRoute = emptyMap(),
        )
    }

    @Test
    fun directTagSpeedReturnsGivenTag() {
        val section = speedSection("V200" to 200.kilometersPerHour)
        val resolution = SpeedLimitResolver.directTagSpeed(section, "V200")
        assertEquals(200.kilometersPerHour, resolution.speed)
    }

    @Test
    fun resolveTagSpeedUsesFallbackWhenDirectTagMissing() {
        val section =
            speedSection(
                "V160" to 160.kilometersPerHour,
                "MA80" to 80.kilometersPerHour,
            )
        val descriptor =
            SpeedLimitTagDescriptor(
                fallbackList = listOf("V160", "MA80"),
            )
        val resolution = SpeedLimitResolver.resolveTagSpeed(section, "V200", descriptor)
        assertEquals(160.kilometersPerHour, resolution.speed)
    }

    @Test
    fun resolveTagSpeedReturnsNullWithoutTrainTag() {
        val section = speedSection("V200" to 200.kilometersPerHour)
        val resolution = SpeedLimitResolver.resolveTagSpeed(section, null, null)
        assertNull(resolution.speed)
    }

    @Test
    fun formatResolutionDescribesFallbackSource() {
        val formatted =
            SpeedLimitResolver.formatResolution(
                SpeedLimitResolver.TagSpeedResolution(
                    140.kilometersPerHour,
                    fr.sncf.osrd.sim_infra.api.SpeedLimitSource.FallbackTag("V140"),
                )
            )
        assertEquals("fallback:V140", formatted)
    }
}
