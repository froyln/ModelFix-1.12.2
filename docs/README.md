# Mod documentation

Model Gap Fix is a client-side LiteLoader mod for Minecraft Java 1.12.2. It adjusts sprite
UV bounds and generated item side faces to reduce visible seams. It is derived from
MehVahdJukaar's [Model Gap Fix](https://github.com/MehVahdJukaar/modelfix-multi), licensed
under GPL-3.0; see [LICENSE](../LICENSE).

- [Architecture and rendering](architecture.md): source layout, loading, algorithms and compatibility.
- [Installation and configuration](configuration.md): requirements, settings and troubleshooting.
- [Building and verification](development.md): toolchain, artifact, tests and visual checks.

The current implementation builds and has ten passing unit tests. A previous build was
confirmed to remove white lines on tools. The latest correction to side-face sampling still
requires an in-game comparison; automated tests do not prove the rendered appearance.
