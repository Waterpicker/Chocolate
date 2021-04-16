/*
 * Part of the Chocolate mod by AlcatrazEscapee.
 * Licensed under the MIT License. See LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate.mixin.world.chunk.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;

import com.alcatrazescapee.chocolate.common.biome.BiomeContainerSerializer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hooks for advanced {@link BiomeArray} serialization
 *
 * @see BiomeContainerSerializer
 */
@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    private static NbtCompound rootNbt;

    @Redirect(method = "deserialize", at = @At(value = "NEW", target = "net/minecraft/world/biome/source/BiomeArray"))
    private static BiomeArray redirect$read$newBiomeContainer(IndexedIterable<Biome> indexedIterable, HeightLimitView world, ChunkPos chunkPos, BiomeSource biomeSource, @Nullable int[] ids) {
        World worldIn = (World) world;

        final NbtCompound levelNbt = rootNbt.getCompound("Level");
        return BiomeContainerSerializer.read(worldIn.getRegistryManager().get(Registry.BIOME_KEY), chunkPos, biomeSource, ids, levelNbt, worldIn);
    }

    @Inject(method = "deserialize", at = @At("HEAD"))
    private static void inject$read(ServerWorld world, StructureManager structureManager, PointOfInterestStorage poiStorage, ChunkPos pos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir) {
        rootNbt = nbt;
    }

    @Inject(method = "serialize", at = @At("RETURN"))
    private static void inject$write(ServerWorld worldIn, Chunk chunkIn, CallbackInfoReturnable<NbtCompound> cir)
    {
        final NbtCompound levelNbt = cir.getReturnValue().getCompound("Level");
        BiomeContainerSerializer.write(chunkIn.getBiomeArray(), levelNbt);
    }
}
