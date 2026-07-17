package fr.sncf.osrd.sim_infra.impl

import fr.sncf.osrd.sim_infra.api.SpeedLimitSource
import fr.sncf.osrd.utils.units.Speed

/**
 * Resolves the effective speed limit contributed by a speed section for a given train tag.
 *
 * Train tags may not be present directly on the infrastructure. When that happens, the SNCF tag
 * hierarchy defined in `assets/static_resources/speed_limit_tags.yml` is walked through the
 * configured fallback list.
 */
object SpeedLimitResolver {

    data class TagSpeedResolution(
        val speed: Speed?,
        val source: SpeedLimitSource?,
    )

    /**
     * Looks up a direct tag speed on the speed section, if any.
     */
    fun directTagSpeed(
        speedSection: SpeedSection,
        trainTag: String,
    ): TagSpeedResolution {
        val speed = speedSection.speedByTrainTag[trainTag]
        return TagSpeedResolution(
            speed = speed,
            source = if (speed != null) SpeedLimitSource.GivenTrainTag(trainTag) else null,
        )
    }

    /**
     * Walks the fallback list and keeps the most permissive speed found on the speed section.
     *
     * When several fallback tags provide a speed on the same section, the highest speed limit must
     * be retained because each fallback tag represents a less specific train category.
     */
    fun resolveFromFallbacks(
        speedSection: SpeedSection,
        descriptor: SpeedLimitTagDescriptor,
        initial: TagSpeedResolution = TagSpeedResolution(null, null),
    ): TagSpeedResolution {
        var resolvedSpeed = initial.speed
        var resolvedSource = initial.source

        for (fallbackTagId in descriptor.fallbackList) {
            val fallbackSpeed = speedSection.speedByTrainTag[fallbackTagId] ?: continue
            if (resolvedSpeed == null || fallbackSpeed < resolvedSpeed) {
                resolvedSpeed = fallbackSpeed
                resolvedSource = SpeedLimitSource.FallbackTag(fallbackTagId)
            }
        }

        return TagSpeedResolution(resolvedSpeed, resolvedSource)
    }

    fun resolveTagSpeed(
        speedSection: SpeedSection,
        trainTag: String?,
        descriptor: SpeedLimitTagDescriptor?,
    ): TagSpeedResolution {
        if (trainTag == null) {
            return TagSpeedResolution(null, null)
        }

        val direct = directTagSpeed(speedSection, trainTag)
        if (direct.speed != null || descriptor == null) {
            return direct
        }
        return resolveFromFallbacks(speedSection, descriptor, direct)
    }

    fun formatResolution(resolution: TagSpeedResolution): String {
        return when (val source = resolution.source) {
            null -> "default"
            is SpeedLimitSource.GivenTrainTag -> "tag:${source.tag}"
            is SpeedLimitSource.FallbackTag -> "fallback:${source.tag}"
            is SpeedLimitSource.UnknownTag -> "unknown-tag"
        }
    }
}
