package com.flix.launcher

import org.junit.Assert.assertEquals
import org.junit.Test

class SpanishTextTest {
    @Test fun settingsSearchAcceptsAccentsAndUppercaseOrPlainRemoteInput() {
        assertEquals(SpanishText.searchKey("PELÍCULAS de ACCIÓN"), SpanishText.searchKey("peliculas de accion"))
        assertEquals(SpanishText.searchKey("TRÁILER"), SpanishText.searchKey("trailer"))
    }

    @Test fun unknownProviderLabelsAndCustomCatalogNamesArePreserved() {
        assertEquals("Mi colección personal", SpanishText.catalog("Mi colección personal"))
        assertEquals("Custom genre", SpanishText.genre("Custom genre"))
    }
}
