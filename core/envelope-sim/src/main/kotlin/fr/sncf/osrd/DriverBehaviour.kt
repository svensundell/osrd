package fr.sncf.osrd

import fr.sncf.osrd.envelope.Envelope
import fr.sncf.osrd.envelope.MRSPEnvelopeBuilder
import fr.sncf.osrd.envelope.part.EnvelopePart
import fr.sncf.osrd.envelope_sim.EnvelopeProfile
import fr.sncf.osrd.path.interfaces.PhysicsPath
import fr.sncf.osrd.utils.OffsetRangeMap
import fr.sncf.osrd.utils.units.Offset
import fr.sncf.osrd.utils.units.meters
import kotlin.math.max
import kotlin.math.min

data class DriverBehaviour(
    val acceleratingPostponementOffset: Double = 50.0,
    val brakingAnticipationOffset: Double = 100.0,
    val signalingSystems: List<String> = listOf("BAL", "BAPR"),
) {
    private val reactionProfile =
        DriverReactionProfile(
            brakingAnticipationOffset = brakingAnticipationOffset,
            acceleratingPostponementOffset = acceleratingPostponementOffset,
        )

    /** Applies the driver behavior to the MRSP, adding reaction time for MRSP changes */
    fun applyToMRSP(
        mrsp: Envelope,
        optSignalingSystemRanges: OffsetRangeMap<PhysicsPath, String>? = null,
    ): Envelope {
        val signalingSystemRanges = optSignalingSystemRanges ?: OffsetRangeMap()
        val builder = MRSPEnvelopeBuilder()
        val totalLength = mrsp.totalDistance
        for (part in mrsp) {
            var begin = part.beginPos
            var end = part.endPos

            val beginSignalingSystem =
                signalingSystemRanges.get(Offset<PhysicsPath>(begin.meters)) ?: ""
            val endSignalingSystem = signalingSystemRanges.get(Offset<PhysicsPath>(end.meters)) ?: ""

            if (
                signalingSystems.contains(beginSignalingSystem) &&
                    reactionProfile.brakingSignalingSystems.contains(beginSignalingSystem)
            ) {
                begin -= reactionProfile.brakingAnticipationOffset
            }
            if (
                signalingSystems.contains(endSignalingSystem) &&
                    reactionProfile.accelerationSignalingSystems.contains(endSignalingSystem)
            ) {
                end += reactionProfile.acceleratingPostponementOffset
            }

            begin = max(0.0, begin)
            end = min(totalLength, end)
            val speed = part.maxSpeed

            builder.addPart(
                EnvelopePart.generateTimes(
                    listOf(
                        EnvelopeProfile.CONSTANT_SPEED,
                        MRSPEnvelopeBuilder.LimitKind.SPEED_LIMIT,
                    ),
                    doubleArrayOf(begin, end),
                    doubleArrayOf(speed, speed),
                )
            )
        }
        return builder.build()
    }
}
