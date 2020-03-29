package br.com.brunoxkk0.sp.mixins.excompressum;

import br.com.brunoxkk0.sp.SyrxPatcher;
import net.blay09.mods.excompressum.ExCompressum;
import net.blay09.mods.excompressum.compat.ExCompressumReloadEvent;
import net.blay09.mods.excompressum.registry.AbstractRegistry;
import net.blay09.mods.excompressum.registry.chickenstick.ChickenStickRegistry;
import net.blay09.mods.excompressum.registry.compressedhammer.CompressedHammerRegistry;
import net.blay09.mods.excompressum.registry.heavysieve.HeavySieveRegistry;
import net.blay09.mods.excompressum.registry.woodencrucible.WoodenCrucibleRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExCompressum.class,remap =  false)
public class MixinExCompressum {

    @Shadow @Final public static Logger logger;

    @Inject(method = "postInit", at = @At("RETURN"))
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event, CallbackInfo callbackInfo) {

        SyrxPatcher.INSTANCE().getLogger().warn("Iniciando injecao de codigo... [ExCompressum.class]");
        SyrxPatcher.INSTANCE().getLogger().info("Recarregando custom drops.");

        AbstractRegistry.registryErrors.clear();
        ChickenStickRegistry.INSTANCE.load(ExCompressum.configDir);
        CompressedHammerRegistry.INSTANCE.load(ExCompressum.configDir);
        HeavySieveRegistry.INSTANCE.load(ExCompressum.configDir);
        WoodenCrucibleRegistry.INSTANCE.load(ExCompressum.configDir);

        logger.info("Recarregado com sucesso.");

        if(AbstractRegistry.registryErrors.size() > 0) {
            logger.warn("There were errors loading the Ex Compressum registries:");
            for(String error : AbstractRegistry.registryErrors) {
                logger.warn("  " + error);
            }
        }

        MinecraftForge.EVENT_BUS.post(new ExCompressumReloadEvent());
        SyrxPatcher.INSTANCE().getLogger().warn("Finalizado injecao de codigo... [ExCompressum.class]");
    }
}
