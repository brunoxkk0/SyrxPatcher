package br.com.brunoxkk0.sp.mod.events;

import br.com.brunoxkk0.sp.mod.SyrxPatcherMod;
import net.minecraft.client.Minecraft;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.client.render.world.SkyblockSkyRenderer;
import vazkii.botania.common.core.handler.ConfigHandler;

public class SkyBoxRenderEvent {

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {

        World world = Minecraft.getMinecraft().world;
        if(ConfigHandler.enableFancySkybox && world.provider.getDimensionType().equals(DimensionType.OVERWORLD)){
            if(!(world.provider.getSkyRenderer() instanceof SkyblockSkyRenderer))
                world.provider.setSkyRenderer(new SkyblockSkyRenderer());
        }

    }

}
