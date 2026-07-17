package fr.sncf.osrd.api.stdcm

import com.google.common.collect.Range
import com.google.common.collect.TreeRangeSet
import fr.sncf.osrd.conflicts.RequirementId
import fr.sncf.osrd.conflicts.RequirementType
import fr.sncf.osrd.sim_infra.api.ZoneId
import fr.sncf.osrd.stdcm.STDCMTimetableData
import java.util.TreeMap

/**
 * Filters timetable spacing requirements against the STDCM search window.
 *
 * Two parallel representations are maintained:
 * - [zoneUses] ranges drive block availability during graph exploration
 * - [detailedRequirements] metadata is attached to post-processing diagnostics
 *
 * Both must stay consistent, but they intentionally use different boundary semantics
 * when deciding whether a requirement intersects the search window.
 */
object RequirementsFilter {

    data class WindowBounds(val beginEpoch: Double, val endEpoch: Double)

    data class FilteredZoneUses(val rangesByZone: Map<ZoneId, TreeRangeSet<Double>>)

    data class FilteredMetadata(
        val entriesByZone: Map<ZoneId, List<STDCMTimetableData.DetailedRequirement>>
    )

    /**
     * Returns true when a zone occupancy range should be considered for STDCM exploration.
     *
     * A range intersects the search window when it starts strictly before the window ends and
     * ends strictly after the window begins.
     */
    fun zoneUseIntersectsWindow(
        range: Range<Double>,
        window: WindowBounds,
    ): Boolean {
        return range.upperEndpoint() > window.beginEpoch && range.lowerEndpoint() < window.endEpoch
    }

    /**
     * Shifts an absolute epoch range into the STDCM-relative timeline.
     */
    fun shiftRangeToRelative(
        range: Range<Double>,
        windowBeginEpoch: Double,
    ): Range<Double> {
        return Range.range(
            range.lowerEndpoint() - windowBeginEpoch,
            range.lowerBoundType(),
            range.upperEndpoint() - windowBeginEpoch,
            range.upperBoundType(),
        )
    }

    fun filterZoneUses(
        zoneUses: Map<ZoneId, TreeRangeSet<Double>>,
        window: WindowBounds,
    ): FilteredZoneUses {
        val requirements = mutableMapOf<ZoneId, TreeRangeSet<Double>>()
        for ((zoneId, rangeSet) in zoneUses) {
            val setBuilder = requirements.computeIfAbsent(zoneId) { TreeRangeSet.create() }
            for (range in rangeSet.asRanges()) {
                if (zoneUseIntersectsWindow(range, window)) {
                    setBuilder.add(shiftRangeToRelative(range, window.beginEpoch))
                }
            }
        }
        return FilteredZoneUses(requirements)
    }

    /**
     * Returns true when detailed requirement metadata should be retained for the search window.
     *
     * Metadata is kept only for requirements fully contained inside the open search window.
     */
    fun detailedRequirementInsideWindow(
        from: Double,
        to: Double,
        window: WindowBounds,
    ): Boolean {
        return from >= window.beginEpoch && to <= window.endEpoch
    }

    fun filterDetailedRequirements(
        detailedRequirements: Map<ZoneId, List<STDCMTimetableData.DetailedRequirement>>,
        window: WindowBounds,
    ): FilteredMetadata {
        val metadata = mutableMapOf<ZoneId, MutableList<STDCMTimetableData.DetailedRequirement>>()
        for ((zoneId, entries) in detailedRequirements) {
            val metadataList = metadata.computeIfAbsent(zoneId) { mutableListOf() }
            for (entry in entries) {
                if (detailedRequirementInsideWindow(entry.from, entry.to, window)) {
                    metadataList.add(
                        STDCMTimetableData.DetailedRequirement(
                            entry.from - window.beginEpoch,
                            entry.to - window.beginEpoch,
                            entry.source,
                        )
                    )
                }
            }
        }
        return FilteredMetadata(metadata)
    }

    fun toParsedRequirements(
        filteredZoneUses: FilteredZoneUses,
    ): Map<ZoneId, TreeMap<Double, Range<Double>>> {
        return filteredZoneUses.rangesByZone.mapValues { rangeSet ->
            TreeMap(rangeSet.value.asRanges().associateBy { it.upperEndpoint() })
        }
    }

    fun describeRequirementSource(source: RequirementId?): String {
        if (source == null) return "unknown"
        return when (source.type) {
            RequirementType.TRAIN -> "train:${source.id}"
            RequirementType.WORK_SCHEDULE -> "work_schedule:${source.id}"
        }
    }
}
