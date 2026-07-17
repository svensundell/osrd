package fr.sncf.osrd

/**
 * Per-signaling-system driver reaction parameters used when applying MRSP offsets.
 */
data class DriverReactionProfile(
    val brakingAnticipationOffset: Double = 100.0,
    val acceleratingPostponementOffset: Double = 50.0,
    val brakingSignalingSystems: Set<String> = setOf("BAL", "BAPR"),
    val accelerationSignalingSystems: Set<String> = setOf("BAPR"),
)
