package br.com.brunoxkk0.sp.mod;


import br.com.brunoxkk0.sp.mod.core.SyrxModContainer;
import br.com.brunoxkk0.sp.mod.events.SkyBoxRenderEvent;
import com.google.common.eventbus.Subscribe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

public class SyrxPatcherMod extends SyrxModContainer {

    public static SyrxPatcherMod instance;
    public Logger logger = getLogger();

    public SyrxPatcherMod(){
        super("syrxpatchermod","SyrxPatcherMod","1.0");
        instance = this;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent event) {
        if(Loader.isModLoaded("botania")){
            if(event.getSide().equals(Side.CLIENT)){
                logger.info("Botania detectado, registrando evento: [SkyBoxRenderEvent.class]" );
                MinecraftForge.EVENT_BUS.register(new SkyBoxRenderEvent());
            }
        }
    }

}
