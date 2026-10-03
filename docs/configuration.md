# Installation and configuration

## Requirements and installation

Use Minecraft Java 1.12.2 with LiteLoader and Java 8. Place the built file
`modelgapfix-liteloader-1.12.2-1.0.0.litemod` in the instance's `minecraft/mods/` directory
or LiteLoader's version folder `minecraft/mods/1.12.2/`. Keep only one active copy and
restart the instance after replacing it. Installation does not require a server mod.

## Settings

The mod reads `<minecraft directory>/config/modelgapfix.properties` once, lazily on its
first configuration access. Create or edit the file yourself and restart to apply changes.
The mod never creates or rewrites it.

```properties
enabled=true
uv_inset=0.0
```

| Key | Default | Behavior |
| --- | --- | --- |
| `enabled` | `true` | Enables both corrections. `false` keeps vanilla item side faces and vanilla sprite inset. |
| `uv_inset` | `0.0` | Texel inset on each side of every sprite. `0.01` restores vanilla sprite bounds. |

Missing keys use their defaults. Invalid boolean text or an unparseable number uses that
key's default and logs a warning. Configuration read exceptions fall back to enabled defaults.
The numeric parser uses `Float.parseFloat` without a range/finite check; use ordinary finite
values near the documented range. Settings are logged under `[ModelGapFix]`.

`uv_inset=0.01` with the mod enabled restores only sprite bounds: generated side-face
replacement remains active. Use `enabled=false` for the complete vanilla rendering baseline.
The item expansion (`0.02` model units) and recess (`0.0001` model units) are code constants.

## Atlas tradeoff and troubleshooting

A smaller sprite inset reduces seams but may expose an adjacent atlas texture with some
filtering or resource packs. If that occurs, try a small inset such as `0.002` and compare
at the same angle. Raising the inset can bring seams back.

If the mod appears inactive, inspect `logs/latest.log` for registration of
`mixins.modelgapfix.json`, the `[ModelGapFix]` configuration line and mixin errors. Config
registration alone does not prove both patches applied. Verify the instance directory and
that LiteLoader discovered the intended `.litemod` file. Check for duplicate versions.

For a startup crash, keep the first Mixin failure in the log: subsequent missing-class or
re-entrance errors can be consequences of that failure. For visual problems, compare the
same item, resource pack, background, angle and scale with `enabled=false` and `enabled=true`.
Record Minecraft/mod versions, mipmap settings and other rendering mods with the result.
