# Chocolate

*Vanilla is good - but chocolate is better. ;)*

This mod exists to fix vanilla bugs - mostly to do with custom world generation - which have a much larger impact on modded.

### Design

In order to fix these features while maintaining *most of* vanilla parity, this mod has been designed with a couple things in mind:

- Obey strong contracts of behavior. For example, enforcing that `BiomeContainer` *must* contain a reference to a `Registry<Biome>` at construction time, as opposed to checking it at serialization time and either assuming, or checking backups for the biome registry. This enforcement makes it much easier to write semantically correct - and verifiable - code, and also makes it easier to identify problems when and where they occur. Which leads to the next important point:
- Identify failure states, and fail fast, and with as much information as possible. In places where failures are possible due to external factors (such as unknown chunk data format changes, or adding behavioral contracts to otherwise unrestricted code), Chocolate has been designed to fail fast - throwing exceptions - and with as much debugging information as possible, in order to identify what assumption has been violated, and try to arrive at a fix.

### Fixes

- Fixes [MC-202036](https://bugs.mojang.com/browse/MC-202036) : Biomes are saved to chunks as raw IDs, and can become shuffled when adding new biomes (either via data packs or mods), or the registration order of existing biomes changes. Chocolate fixes this by adding a palette to the chunk save data, and guards biome serialization with this registry key based approach.
- Fixes [MC-197616](https://bugs.mojang.com/browse/MC-197616) : Using a data pack biome with the "Single Biome" world preset causes massive log spam, and invalid biomes to be detected on client. This occurs in vanilla due to biomes not existing in the runtime dynamic registry instance. Chocolate fixes this by tracking biome registry keys, and transmuting biomes at runtime where nessecary into their current registry present objects.

### Possibilities

I am open to fixing other potential issues in this mod. However, due to my own area of expertise, and plan for this mod, requirements for possible future vanilla and/or forge bugfixes are:

- It needs to be deterministically fixable, without breaking vanilla contracts or causing undue pain to other mods which may rely on some vanilla behavior.
- It should be a high visibility or high impact bug that predominantly affects modded or data pack world generation.
- It is something which cannot be avoided or skirted around using the standard set of tools which are available for mods.

### Usage (For Modders)

To add this mod as a dependency in dev, first add jitpack to your `repositories` section. **Do not** add this to your `buildscript` section!

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Secondly, add the version of Chocolate you desire to the `dependencies` section. `VERSION` and `MINECRAFT_VERSION` must be replaced with the relevant versions, or another valid jitpack version identifier (such as a commit hash). Make sure to include `transitive = false` due to [ForgeGradle#584](https://github.com/MinecraftForge/ForgeGradle/issues/584). Latest tagged versions can be checked in the "Releases" tab on github.

```groovy
dependencies {
    testImplementation fg.deobf('com.github.alcatrazEscapee:chocolate:VERSION-MINECRAFT_VERSION') {
        transitive = false
    }
}
```