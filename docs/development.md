# Building and verification

## Toolchain

The project targets Java 8 and uses the committed Gradle 4.10.3 wrapper. ForgeGradle
`2.3-SNAPSHOT` provides the LiteLoader workspace, Minecraft 1.12.2 decompilation,
`stable_39` MCP mappings and production reobfuscation. MixinGradle `0.6-SNAPSHOT`
generates `mixins.modelgapfix.refmap.json`; compile-time Mixin processing uses 0.7.4.
The runtime supplied by a LiteLoader installation may be 0.7.5.

```bash
JAVA_HOME=/usr/lib/jvm/java-8-openjdk ./gradlew build
```

Use an appropriate Java 8 installation path on other systems. Gradle 4.10.3 does not run
on this machine's default Java 21. The first workspace build needs dependency downloads
and Minecraft processing; later builds reuse the cache. Add `--offline` only with a warm
cache. Output is `build/libs/modelgapfix-liteloader-1.12.2-1.0.0.litemod`.

The build includes tests, Mixin annotation processing, packaging and reobfuscation. Inspect
the output for target ambiguity warnings even if it exits zero. `build.number`, generated
outputs, runtime logs and local agent/planning files are excluded from version control.

## Automated checks

```bash
JAVA_HOME=/usr/lib/jvm/java-8-openjdk ./gradlew test
JAVA_HOME=/usr/lib/jvm/java-8-openjdk ./gradlew build
```

| Test class | Cases | What it checks |
| --- | --- | --- |
| `GeneratedItemEdgesTest` | 4 | Transparent breaks, opaque UV centers, tint/texture retention, one-pixel/non-square fixture, animated boundaries, changing-run overlap, solid edge merging |
| `ModelFixEnlargeFacesTest` | 6 | Four side directions, multi-face exclusion, unchanged NORTH/SOUTH geometry |

Tests run against public helpers and synthetic sprites/parts in a headless deobfuscated
workspace. They do not execute transformed runtime mixins, render pixels or verify every
resource pack. Config parsing and disabled runtime behavior currently require manual checks.
The existing NORTH/SOUTH test exercises NORTH; it is not separate coverage for SOUTH.

## Manual verification

Use an existing Prism instance with its assets already downloaded. The development
`./gradlew runClient` is available, but an empty asset cache can stall it on downloads.
Copy the built `.litemod` into the chosen instance's mods directory and launch that instance.

1. Inspect a fresh startup log for mixin prepare/apply errors and the enabled configuration.
2. Compare diamond pickaxe and sword edges against high-contrast backgrounds at several
   angles and scales. Check both white lines and newly exposed transparent texture strips.
3. Test animated and layered generated items, plus custom multi-face models. Look for
   texture bleeding, distorted silhouettes, overlapping faces and flickering.
4. Set `enabled=false`, restart and compare with no mod. Sprite bounds should use the
   vanilla `0.01f` inset and vanilla generated side faces should remain untouched.
5. Set `enabled=true` and `uv_inset=0.01`; verify vanilla sprite bounds while remembering
   the independent generated-item correction remains enabled.
6. Test an absent config and malformed ordinary values. Confirm safe defaults in the log
   and that the mod does not create the config file.

The current ten-test suite and build pass. The latest side-face sampling correction still
needs a matched in-game comparison. A successful build is not visual acceptance.

## Extension constraints

Keep patches in `dev.froyln.modelgapfix.mixin` and helpers outside that package. Register
new client patches in the existing config. Write mixins in MCP names and let the refmap
handle production names. Target overloads with full descriptors. Preserve upstream credit
for derived algorithms and the GPL-3.0 license. Avoid adding server or network behavior to
this client rendering mod.
