# Model Gap Fix — Minecraft 1.12.2 LiteLoader

A client-side port of MehVahdJukaar's [Model Gap Fix](https://github.com/MehVahdJukaar/modelfix-multi)
that adjusts sprite UV bounds and generated item edges to reduce model seams. No gameplay
changes, network calls or server component. Licensed under [GPL-3.0](LICENSE).

## Install

Requires Minecraft Java 1.12.2 and LiteLoader. Put
`modelgapfix-liteloader-1.12.2-1.0.0.litemod` in your instance's `minecraft/mods/` or
`minecraft/mods/1.12.2/`, then restart.

## Build

```bash
JAVA_HOME=/usr/lib/jvm/java-8-openjdk ./gradlew build
```

Use Java 8 with the committed wrapper. The artifact appears in `build/libs/`. The first
build downloads dependencies and prepares Minecraft; `--offline` needs a populated cache.

## Configuration

Create `minecraft/config/modelgapfix.properties` if you want to change defaults:

```properties
enabled=true
uv_inset=0.0
```

The mod only reads the file. `enabled=false` restores vanilla behavior. `uv_inset=0.01`
restores vanilla sprite bounds while keeping the independent item fix enabled.

## Documentation and status

Start with [docs/README.md](docs/README.md) for architecture, rendering algorithms,
configuration, troubleshooting, building and verification.

The implementation builds and passes ten unit tests. An earlier build removed the reported
white tool seams; the latest texture-sampling correction still needs in-game confirmation.
