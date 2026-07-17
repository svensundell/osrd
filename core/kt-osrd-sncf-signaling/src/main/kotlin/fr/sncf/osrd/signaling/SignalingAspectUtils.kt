package fr.sncf.osrd.signaling

/**
 * Shared helpers for interpreting signal aspects across SNCF signaling systems.
 */
object SignalingAspectUtils {

    /**
     * Returns whether the given aspect should be treated as a "voie libre" (clear) indication.
     *
     * TVM aspects encode VL in multiple textual forms. All of them must be ignored when deciding
     * whether a signal is constraining on sight.
     */
    fun isVoieLibreAspect(aspect: String, signalingSystem: String): Boolean {
        return when (signalingSystem) {
            "TVM300", "TVM430" -> aspect == "300VL"
            else -> aspect.contains("VL")
        }
    }

    fun aspectFamily(aspect: String): String {
        return when {
            aspect.contains("VL") -> "voie-libre"
            aspect.contains("A") -> "avertissement"
            aspect == "000" -> "arret"
            aspect == "OCCUPIED" || aspect == "RRR" -> "occupied"
            else -> "other"
        }
    }
}
