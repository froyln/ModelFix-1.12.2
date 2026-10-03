package dev.froyln.modelgapfix;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import net.minecraft.launchwrapper.Launch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Flat {@code modelgapfix.properties}: one knob per line, parsed with {@link Properties}.
 * Missing key or unreadable file falls back to that key's default. The mod <b>never writes the
 * file</b>; if it is absent the defaults and the path are logged once so the user can create it.
 *
 * <p>Loaded lazily on first read, because the accessors are called from inside a class
 * transformer. Nothing here may throw: the whole load is guarded, since it runs before
 * Minecraft is up.
 */
public final class Config {
    public static final Logger LOG = LogManager.getLogger("ModelGapFix");

    private static final String PATH = "config/modelgapfix.properties";
    private static final float VANILLA_UV_INSET = 0.01f;

    private static boolean enabled = true;
    private static float uvInset = 0.0f;
    private static boolean loaded;

    private Config() {
    }

    /**
     * Texel inset applied to every side of every sprite UV. {@code 0.0} removes the model gaps,
     * {@code 0.01} is exactly vanilla. When the mod is off this is vanilla, so the caller needs
     * no branch of its own.
     */
    public static float uvInset() {
        load();
        return enabled ? uvInset : VANILLA_UV_INSET;
    }

    /** Quad growth for 2D item models; disabled returns zero. */
    public static double quadExpansion() {
        return enabled() ? ModelFix.EXPANSION : 0.0d;
    }

    /** How far a 2D item quad moves toward the item's centre. */
    public static double quadIndent() {
        return enabled() ? ModelFix.INDENT : 0.0d;
    }

    public static boolean enabled() {
        load();
        return enabled;
    }

    private static synchronized void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        Properties props = new Properties();
        try {
            File dir = Launch.minecraftHome;
            if (dir == null) {
                LOG.warn("[ModelGapFix] no Minecraft home yet; using defaults");
            } else {
                File file = new File(dir, PATH);
                if (file.isFile()) {
                    try (InputStream in = new FileInputStream(file)) {
                        props.load(in);
                    }
                } else {
                    LOG.info("[ModelGapFix] {} not found; using defaults", file);
                }
            }
            enabled = flag(props, "enabled", true);
            uvInset = number(props, "uv_inset", 0.0f);
        } catch (Exception e) {
            LOG.warn("[ModelGapFix] config unreadable ({}); using defaults", e.toString());
            props.clear();
            enabled = true;
            uvInset = 0.0f;
        }
        LOG.info("[ModelGapFix] enabled={} uv_inset={}", enabled, uvInset);
    }

    private static boolean flag(Properties props, String key, boolean fallback) {
        String raw = props.getProperty(key);
        if (raw == null) {
            return fallback;
        }
        raw = raw.trim();
        if ("true".equalsIgnoreCase(raw)) {
            return true;
        }
        if ("false".equalsIgnoreCase(raw)) {
            return false;
        }
        LOG.warn("[ModelGapFix] {}={} is not a boolean; using {}", key, raw, fallback);
        return fallback;
    }

    private static float number(Properties props, String key, float fallback) {
        String raw = props.getProperty(key);
        if (raw == null) {
            return fallback;
        }
        try {
            return Float.parseFloat(raw.trim());
        } catch (NumberFormatException e) {
            LOG.warn("[ModelGapFix] {}={} is not a number; using {}", key, raw, fallback);
            return fallback;
        }
    }
}
