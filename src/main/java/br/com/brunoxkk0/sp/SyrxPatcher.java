package br.com.brunoxkk0.sp;


import br.com.brunoxkk0.sp.mod.SyrxPatcherMod;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import sun.misc.URLClassPath;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 10001)
@IFMLLoadingPlugin.Name("SyrxPatcher")
public class SyrxPatcher implements IFMLLoadingPlugin {

    private LaunchClassLoader classLoader;
    private Logger logger = LogManager.getLogger("SyrxPatcher");
    private static SyrxPatcher instance;

    public static SyrxPatcher INSTANCE(){
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public LaunchClassLoader getClassLoader() {
        return classLoader;
    }

    public SyrxPatcher(){

        instance = this;
        System.setProperty("mixin.debug.export", "true");

        if (!(SyrxPatcher.class.getClassLoader() instanceof LaunchClassLoader))
            throw new RuntimeException("Coremod plugin class was loaded by another classloader!");

        classLoader = (LaunchClassLoader) SyrxPatcher.class.getClassLoader();

        logger.warn(" ");
        logger.warn("Adicionando compatibilidade com mods.");
        fixMixinClasspathOrder();
        MixinBootstrap.init();

        MixinEnvironment.Side side;
        if((side = MixinEnvironment.getCurrentEnvironment().getSide()).equals(MixinEnvironment.Side.CLIENT)){
            getLogger().warn("Loading CLIENT SIDE");
            Mixins.addConfiguration("mixin.syrxpatcher.json");
        }else if(side.equals(MixinEnvironment.Side.SERVER)) {
            getLogger().warn("Loading SERVER SIDE");
            loadServerAllMixins();
        }else {
            getLogger().error("Fail to load mixins... UNKNOWN SIDE");
        }

        LogManager.getLogger().warn(" ");

    }


    public void loadClientAllMixins(){
        logger.warn("Carregando Mixins Client Side...");
        Mixins.addConfiguration("mixins/mixin.excompressum.json");
    }

    public void loadServerAllMixins(){
        logger.warn("Carregando Mixins Server Side...");
        loadMixin("mixins/mixin.excompressum.json", "excompressum");
    }

    private void loadMixin(String configuration, String... modName){

        File mods_folder = new File("mods");
        boolean canLoad = false;

        List<File> files = new ArrayList<>();
        for(String mod : modName){
            File f;
            if((f = scanForMod(mods_folder, mod)) != null){
                files.add(f);
                canLoad = true;
            }else {
                canLoad = false;
                break;
            }
        }

        if(canLoad){

            try{

                for(File mjar : files){
                    loadModJar(mjar);
                }

                Mixins.addConfiguration(configuration);

                return;
            }catch (Exception e){
                logger.error("Fail to load " + configuration);
                logger.error(e.getMessage());
                return;
            }
        }

        logger.error("Fail to load " + configuration);
    }

    private File scanForMod(File dir, String modid){

        File file = null;

        for (File mod : FileUtils.listFiles(dir, new String[]{"jar"}, true)) {

            if (!mod.canRead()) continue;

            try {

                ZipFile zipFile = new ZipFile(mod);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {

                    ZipEntry entry = entries.nextElement();

                    if(entry.getName().equals("mcmod.info")){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), StandardCharsets.UTF_8));

                        String line;
                        while ((line = bufferedReader.readLine()) != null){
                            if(line.contains("modid")){
                                line = line.replace(" ","");
                                line = line.replace("\"", "");
                                line = line.replace("modid:","");
                                line = line.replace(",","");

                                if(line.equals(modid)){
                                    return mod;
                                }
                            }
                        }

                    }

                }

            } catch (Exception e) {
                logger.error("Impossible to found " + modid + " mod.");
                e.printStackTrace();
            }
        }

        return file;
    }

    private void loadModJar(File jar) throws Exception{
        ((LaunchClassLoader) this.getClass().getClassLoader()).addURL(jar.toURI().toURL());
        CoreModManager.getReparseableCoremods().add(jar.getName());
    }


    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return SyrxPatcherMod.class.getCanonicalName();
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private static void fixMixinClasspathOrder() {
        URL url = SyrxPatcher.class.getProtectionDomain().getCodeSource().getLocation();
        givePriorityInClasspath(url, Launch.classLoader);
        givePriorityInClasspath(url, (URLClassLoader) ClassLoader.getSystemClassLoader());
    }

    private static void givePriorityInClasspath(URL url, URLClassLoader classLoader) {
        try {
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucpField.setAccessible(true);

            List<URL> urls = new ArrayList<>(Arrays.asList(classLoader.getURLs()));
            urls.remove(url);
            urls.add(0, url);
            URLClassPath ucp = new URLClassPath(urls.toArray(new URL[0]));

            ucpField.set(classLoader, ucp);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

}
