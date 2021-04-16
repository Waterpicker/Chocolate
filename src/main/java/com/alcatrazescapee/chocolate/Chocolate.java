/*
 * Part of the Chocolate mod by AlcatrazEscapee.
 * Licensed under the MIT License. See LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;

import com.alcatrazescapee.chocolate.common.Debug;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

//@Mod(Chocolate.MOD_ID)
public final class Chocolate implements ModInitializer {
    public static final String MOD_ID = "chocolate";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static MinecraftServer server;

    @Override
    public void onInitialize() {
        LOGGER.info("Vanilla is real good, but chocolate is better, let's be honest. :)");

        ServerLifecycleEvents.SERVER_STARTING.register(a -> {
            server = a;
        });

        //ChocolateConfig.init();
        Debug.init();
    }
}