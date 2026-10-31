package com.cerbon.bosses_of_mass_destruction.neoforge.event;

import com.cerbon.bosses_of_mass_destruction.block.BMDBlockEntities;
import com.cerbon.bosses_of_mass_destruction.config.BMDConfig;
import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.bosses_of_mass_destruction.item.BMDItems;
import com.cerbon.bosses_of_mass_destruction.particle.BMDParticles;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = BMDConstants.MOD_ID, value = Dist.CLIENT)
public class BMDClientEventsNeo {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BMDItems.initClient();
            BMDEntities.initClient();
            BMDBlockEntities.initClient();
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, screen) -> AutoConfigClient.getConfigScreen(BMDConfig.class, screen).get());
        });
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        // CERBONs API SimpleParticle extends TextureSheetParticle which was removed in NeoForge 26.1.2
        // Particle providers will need to be re-implemented using the new particle system
        // TODO: Re-implement custom particle providers using vanilla Minecraft particle API
    }
}
