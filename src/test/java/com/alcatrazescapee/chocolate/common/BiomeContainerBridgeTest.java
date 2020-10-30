package com.alcatrazescapee.chocolate.common;

import java.util.Arrays;

import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraft.world.biome.Biomes;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.alcatrazescapee.chocolate.common.biome.BiomeContainerBridge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
public class BiomeContainerBridgeTest
{
    @Test
    public void testBiomeContainerBridge()
    {
        // Assert that the biome container mixin was successful
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final Object biomeContainer = new BiomeContainer(registry, new Biome[BiomeContainer.BIOMES_SIZE]);

        assertTrue(biomeContainer instanceof BiomeContainerBridge);
    }

    @Test
    public void testBiomeContainerThrowsInvalidRegistry()
    {
        // Assert that the biome container will throw when given an invalid registry subclass
        final IObjectIntIterable<Biome> fakeRegistry = new IntIdentityHashBiMap<>(10);

        assertThrows(IllegalArgumentException.class, () -> new BiomeContainer(fakeRegistry, new Biome[BiomeContainer.BIOMES_SIZE]));
    }

    @Test
    public void testBiomeContainerRegistry()
    {
        // Assert the biome container's registry is populated correctly
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final BiomeContainer container = new BiomeContainer(registry, new Biome[BiomeContainer.BIOMES_SIZE]);
        final BiomeContainerBridge bridge = (BiomeContainerBridge) container;

        assertSame(registry, bridge.bridge$getActualBiomeRegistry());
    }

    @Test
    public void testBiomeContainerBiomes()
    {
        // Assert the biome container's biomes is populated and returned correctly
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final Biome[] biomes = new Biome[BiomeContainer.BIOMES_SIZE];
        for (int i = 0; i < biomes.length; i++)
        {
            biomes[i] = BiomeMaker.theVoidBiome();
        }
        final BiomeContainer container = new BiomeContainer(registry, biomes);
        final BiomeContainerBridge bridge = (BiomeContainerBridge) container;

        assertSame(biomes, bridge.bridge$getInternalBiomeArray());
        assertArrayEquals(biomes, bridge.bridge$getInternalBiomeArray());
    }

    @Test
    public void testWriteInvalidBiomesWithKey()
    {
        // Assert invalid biomes are written correctly
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final Biome[] biomes = new Biome[BiomeContainer.BIOMES_SIZE];
        final Biome invalidBiome = BiomeMaker.theVoidBiome().setRegistryName(Biomes.BADLANDS.location());
        Arrays.fill(biomes, invalidBiome);

        final BiomeContainer container = new BiomeContainer(registry, biomes);

        int[] expectedIds = new int[BiomeContainer.BIOMES_SIZE];
        int biomeId = registry.getId(registry.get(Biomes.BADLANDS));
        Arrays.fill(expectedIds, biomeId);

        assertArrayEquals(expectedIds, container.writeBiomes());
    }

    @Test
    public void testWriteValidBiomes()
    {
        // Assert valid biomes are written correctly
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final Biome[] biomes = new Biome[BiomeContainer.BIOMES_SIZE];
        final Biome validBiome = registry.get(Biomes.BAMBOO_JUNGLE);
        Arrays.fill(biomes, validBiome);

        final BiomeContainer container = new BiomeContainer(registry, biomes);

        int[] expectedIds = new int[BiomeContainer.BIOMES_SIZE];
        int biomeId = registry.getId(validBiome);
        Arrays.fill(expectedIds, biomeId);

        assertArrayEquals(expectedIds, container.writeBiomes());
    }
}
