package com.liskovsoft.googlecommon.common.locale;

import com.liskovsoft.sharedutils.locale.LocaleUpdater;
import com.liskovsoft.sharedutils.locale.LocaleUtility;
import com.liskovsoft.sharedutils.prefs.GlobalPreferences;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class LocaleManager {
    private static LocaleManager sInstance;
    private String mLang;
    private String mCountry;
    private int mOffsetFromUtcMinutes;

    private LocaleManager() {
        initLang();
        initUtcOffset();
    }

    public static LocaleManager instance() {
        if (sInstance == null) {
            sInstance = new LocaleManager();
        }

        return sInstance;
    }

    public static void unhold() {
        sInstance = null;
    }

    public String getLanguage() {
        return mLang;
    }

    /**
     * Used in tests only
     */
    public void setLanguage(String lang) {
        mLang = lang;
    }

    public String getCountry() {
        return mCountry;
    }

    /**
     * Used in tests only
     */
    public void setCountry(String country) {
        mCountry = country;
    }

    public int getUtcOffsetMinutes() {
        return mOffsetFromUtcMinutes;
    }

    private void initLang() {
        Locale locale = getLocale();
        mLang = youTubeLanguage(locale);
        mCountry = locale.getCountry();
    }

    /** YouTube's own hl codes: every one measured to answer 200 (2026-11-18). */
    private static final Set<String> YOUTUBE_LANGUAGES = new HashSet<>(Arrays.asList(
            "af", "am", "ar", "as", "az", "be", "bg", "bn", "bs", "ca", "cs", "da", "de", "el", "en", "en-GB",
            "en-IN", "es", "es-419", "es-US", "et", "eu", "fa", "fi", "fil", "fr", "fr-CA", "gl", "gu", "hi",
            "hr", "hu", "hy", "id", "is", "it", "iw", "ja", "ka", "kk", "km", "kn", "ko", "ky", "lo", "lt",
            "lv", "mk", "ml", "mn", "mr", "ms", "my", "ne", "nl", "no", "or", "pa", "pl", "pt", "pt-PT", "ro",
            "ru", "si", "sk", "sl", "sq", "sr", "sr-Latn", "sv", "sw", "ta", "te", "th", "tr", "uk", "ur", "uz",
            "vi", "zh-CN", "zh-HK", "zh-TW", "zu"));

    /**
     * The phone's locale as an hl YouTube takes. A raw BCP-47 tag is refused with HTTP 400 when its
     * region is not one YouTube offers (en-SA, en-CA, de-AT, fr-BE...), and so is a language it does not
     * support at all (cy, ga...): every request failed on such a phone (issue #263).
     */
    static String youTubeLanguage(Locale locale) {
        String lang = locale.getLanguage();
        String country = locale.getCountry();
        switch (lang) {
            case "he": lang = "iw"; break;
            case "in": lang = "id"; break;
            case "nb": case "nn": lang = "no"; break;
            case "tl": lang = "fil"; break;
            case "zh":
                if ("HK".equals(country) || "MO".equals(country)) return "zh-HK";
                return "TW".equals(country) || "Hant".equals(locale.getScript()) ? "zh-TW" : "zh-CN";
            case "es":
                if (!country.isEmpty() && !"ES".equals(country) && !"US".equals(country)) return "es-419";
                break;
            case "sr":
                if ("Latn".equals(locale.getScript())) return "sr-Latn";
                break;
        }
        String tag = lang + "-" + country;
        if (YOUTUBE_LANGUAGES.contains(tag)) return tag;
        return YOUTUBE_LANGUAGES.contains(lang) ? lang : "en";
    }

    private void initUtcOffset() {
        TimeZone tz = TimeZone.getDefault();
        Date now = new Date();
        mOffsetFromUtcMinutes = tz.getOffset(now.getTime()) / 1_000 / 60;
    }

    private Locale getLocale() {
        Locale locale;

        // Proper locale
        if (GlobalPreferences.isInitialized()) {
            locale = LocaleUpdater.getSavedLocale(GlobalPreferences.sInstance.getContext());

            if (locale == null) {
                locale = LocaleUtility.getCurrentLocale(GlobalPreferences.sInstance.getContext());
            }
        } else { // Fallback locale
            locale = Locale.getDefault();
        }

        return locale;
    }
}
