/*
 * Part of the Chocolate mod by AlcatrazEscapee.
 * Licensed under the MIT License. See LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate.mixin.world.biome;

import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;

import com.alcatrazescapee.chocolate.common.biome.BiomeContainerBridge;
import com.alcatrazescapee.chocolate.common.biome.BiomeProxy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeArray.class)
public abstract class BiomeContainerMixin implements BiomeContainerBridge
{
    @Shadow @Final private Biome[] data;

    /**
     * A copy of the biome registry, with a narrower type than the super class requires
     */
    private Registry<Biome> chocolate$biomeRegistry;

    @Override
    public Registry<Biome> bridge$getActualBiomeRegistry()
    {
        return chocolate$biomeRegistry;
    }

    @Override
    public Biome[] bridge$getInternalBiomeArray()
    {
        return data;
    }

    /**
     * Verifies that the passed in biome registry was of a wider type than vanilla assumes it to be.
     * This is *technically* breaking the vanilla contract here, and it may cause issues.
     * This is done in order to ensure that when serializing, we have access to a full registry, and can safeguard against potential error propagation later.
     */
    @Inject(method = "<init>(Lnet/minecraft/util/collection/IndexedIterable;Lnet/minecraft/world/HeightLimitView;[Lnet/minecraft/world/biome/Biome;)V", at = @At("RETURN"))
    private void inject$init(IndexedIterable<Biome> indexedIterable, HeightLimitView world, Biome[] data, CallbackInfo ci)
    {
        chocolate$biomeRegistry = BiomeProxy.proxy(indexedIterable);
    }

    /**
     * Modify biome serialization to do two important things:
     * 1. (A minor optimization) - don't unduly query registries for IDs, should be faster as this will not often have many different biome IDs
     * 2. (The important fix) - Instead of directly serializing biome -> (registry) -> int, use {@link BiomeBridge} to go biome -> (bridge) -> registry key -> (registry) -> int
     */
    @Inject(method = "toIntArray()[I", at = @At(value = "HEAD"), cancellable = true)
    private void inject$writeBiomes(CallbackInfoReturnable<int[]> cir)
    {
        BiomeProxy.proxy(data, chocolate$biomeRegistry, cir);
//
//                final int[] biomeIds = new int[data.length];
//
//                Biome lastBiome = null;
//                int lastId = -1;
//
//                for (int i = 0; i < data.length; i++) {
//                    final Biome biome = data[i];
//                    if (biome != lastBiome) {
//                        lastBiome = biome;
//                        lastId = chocolate$biomeRegistry.getRawId(biome);
//                    }
//                    biomeIds[i] = lastId;
//                }
//                cir.setReturnValue(biomeIds);
    }
}
