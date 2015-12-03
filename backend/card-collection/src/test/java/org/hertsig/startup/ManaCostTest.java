package org.hertsig.startup;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ManaCostTest {
    @Test
    public void testEmpty() {
        assertEquals(null, ContentUpgrade.mapManaCost(null));
        assertEquals("", ContentUpgrade.mapManaCost(""));
    }

    @Test
    public void testNumber() {
        assertEquals("1", ContentUpgrade.mapManaCost("{1}"));
        assertEquals("15", ContentUpgrade.mapManaCost("{15}"));
    }

    @Test
    public void testSimpleSymbol() {
        assertEquals("WB", ContentUpgrade.mapManaCost("{W}{B}"));
    }

    @Test
    public void testHybridSymbol() {
        assertEquals("{WB}", ContentUpgrade.mapManaCost("{W/B}"));
        assertEquals("{2B}", ContentUpgrade.mapManaCost("{2/B}"));
        assertEquals("{HB}", ContentUpgrade.mapManaCost("{hb}"));
    }

    @Test
    public void testCombo() {
        assertEquals("X2W{UG}{GP}", ContentUpgrade.mapManaCost("{X}{2}{W}{U/G}{G/P}"));
    }
}
