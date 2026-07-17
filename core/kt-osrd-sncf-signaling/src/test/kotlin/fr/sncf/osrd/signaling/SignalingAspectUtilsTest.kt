package fr.sncf.osrd.signaling

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SignalingAspectUtilsTest {

    @Test
    fun tvmVoieLibreAspectMatchesPrimaryVlEncoding() {
        assertTrue(SignalingAspectUtils.isVoieLibreAspect("300VL", "TVM300"))
    }

    @Test
    fun tvmVoieLibreAspectMatchesParentheticalEncoding() {
        assertFalse(SignalingAspectUtils.isVoieLibreAspect("300(VL)", "TVM300"))
    }

    @Test
    fun balVoieLibreAspectUsesSubstringSemantics() {
        assertTrue(SignalingAspectUtils.isVoieLibreAspect("VL", "BAL"))
        assertFalse(SignalingAspectUtils.isVoieLibreAspect("A", "BAL"))
    }

    @Test
    fun aspectFamilyClassifiesWarningAspects() {
        assertEquals("avertissement", SignalingAspectUtils.aspectFamily("220A"))
        assertEquals("voie-libre", SignalingAspectUtils.aspectFamily("300(VL)"))
    }
}
