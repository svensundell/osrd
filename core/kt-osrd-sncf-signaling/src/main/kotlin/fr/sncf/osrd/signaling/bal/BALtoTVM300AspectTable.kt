package fr.sncf.osrd.signaling.bal

/**
 * Documents how BAL aspects are projected when the next signaling system is TVM300.
 *
 * The cascade is conservative: any restricting BAL aspect maps to a TVM stop or limiting aspect
 * before the movement authority is extended.
 */
object BALtoTVM300AspectTable {
    val primaryAspectMap: Map<String, String> =
        mapOf(
            "VL" to "300VL",
            "A" to "300VL",
            "S" to "080A",
            "C" to "000",
        )

    fun supportedAspects(): Set<String> = primaryAspectMap.keys
}
