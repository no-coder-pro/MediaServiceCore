package com.liskovsoft.googlecommon.common.locale;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Test;

public class LocaleManagerTest {
    private static String hl(String tag) {
        return LocaleManager.youTubeLanguage(Locale.forLanguageTag(tag));
    }

    @Test
    public void mapsToAnHlYouTubeTakes() {
        assertEquals("en", hl("en-SA-u-mu-celsius")); // issue #263
        assertEquals("ar", hl("ar-SA"));
        assertEquals("de", hl("de-AT"));
        assertEquals("en-GB", hl("en-GB"));
        assertEquals("en-IN", hl("en-IN"));
        assertEquals("es-419", hl("es-MX"));
        assertEquals("es", hl("es-ES"));
        assertEquals("es-US", hl("es-US"));
        assertEquals("zh-TW", hl("zh-Hant-TW"));
        assertEquals("zh-HK", hl("zh-Hant-HK"));
        assertEquals("zh-CN", hl("zh-Hans-CN"));
        assertEquals("sr-Latn", hl("sr-Latn-RS"));
        assertEquals("iw", hl("he-IL"));
        assertEquals("id", hl("id-ID"));
        assertEquals("no", hl("nb-NO"));
        assertEquals("en", hl("cy-GB"));
    }
}
