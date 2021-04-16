package com.alcatrazescapee.chocolate.common.biome;

import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class BiomeProxy {
	public static void proxy(Biome[] data, Registry<Biome> chocolate$biomeRegistry, CallbackInfoReturnable<int[]> cir) {
		try {

			final int[] biomeIds = new int[data.length];

			Biome lastBiome = null;
			int lastId = -1;

			for (int i = 0; i < data.length; i++) {
				final Biome biome = data[i];
				if (biome != lastBiome) {
					lastBiome = biome;
					lastId = chocolate$biomeRegistry.getRawId(biome);
				}
				biomeIds[i] = lastId;
			}
			cir.setReturnValue(biomeIds);
		} catch (Exception e) {
			System.out.println("DEER");
			e.printStackTrace();
		}
	}

	public static Registry<Biome> proxy(IndexedIterable<Biome> indexedIterable) {
		if (!(indexedIterable instanceof Registry))
		{
			throw new IllegalArgumentException("[Please Report this to Chocolate!] Biome Registry was not a subclass of Registry<Biome>. This is very bad and will cause many problems!");
		}
		return (Registry<Biome>) indexedIterable;
	}
}
