package com.landscapesreimagined.createresourcevents.Config;

import com.google.common.io.Files;
import com.google.gson.*;
import com.landscapesreimagined.createresourcevents.Blocks.ActiveResourceVentBlock;
import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.landscapesreimagined.createresourcevents.CreateResourceVents;
import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = CreateResourceVents.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{

    public ArrayList<ConfigReloadListener> reloadListeners = new ArrayList<>();

    public static ConfigSerializer SERIALIZATION = new ConfigSerializer();

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(BlockStateHolder.class, BlockStateHolder.SERIALIZATION)
            .registerTypeAdapter(ResourceVentHolder.class, ResourceVentHolder.SERIALIZATION)
            .registerTypeAdapter(Config.class, SERIALIZATION)
            .setPrettyPrinting()
            .create();

    private static final Logger LOGGER = LogUtils.getLogger();

    public static Config INSTANCE;

    public ArrayList<ResourceVentHolder> vents = new ArrayList<>();

    public Config(){}

    public static void loadConfig(File configFile) {

        Config newConfig;
        try{
            Reader fileReader = Files.newReader(configFile, Charset.defaultCharset());

            newConfig = gson.fromJson(fileReader, Config.class);
        } catch(FileNotFoundException e){
            newConfig = new Config();

            newConfig.createDefaultConfig();

            String configJson = gson.toJson(newConfig, Config.class);

            try {
                Files.asCharSink(configFile, Charset.defaultCharset()).write(configJson);
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }


        }

        //notify ActiveResourceVentBlocks, and anyone else who needs updating from config
        for(BlockEntry<ActiveResourceVentBlock> ventBlock : DynamicRegistry.DORMANT_ACTIVE_MAP.values()){
            ventBlock.get().reload(newConfig);
        }

        if(INSTANCE != null) {
            for (ConfigReloadListener listener : INSTANCE.reloadListeners) {
                listener.reloadConfig(newConfig);
                newConfig.reloadListeners.add(listener);
            }
            INSTANCE.reloadListeners.clear();
        }

        INSTANCE = newConfig;


    }

    @FunctionalInterface
    public interface ConfigReloadListener{
        void reloadConfig(Config newConfig);
    }

    @NotNull
    public ResourceVentHolder getVent(String ventName){
        for(ResourceVentHolder h : this.vents){
            if(h.ventName.equals(ventName)){
                return h;
            }
        }
        return this.vents.stream().findFirst().get();
    }


    private void createDefaultConfig(){
        ResourceVentHolder asurine = new ResourceVentHolder("asurine");

        asurine.addGenerationState(new BlockStateHolder(Create.asResource("asurine")))
                .addCatalystFluid(ResourceLocation.parse("lava"));

        vents.add(asurine);

        ResourceVentHolder veridium = new ResourceVentHolder("veridium");

        veridium.addGenerationState(new BlockStateHolder(Create.asResource("veridium")))
                .addCatalystFluid(ResourceLocation.parse("lava"));

        vents.add(veridium);

        ResourceVentHolder crimsite = new ResourceVentHolder("crimsite");

        crimsite.addGenerationState(new BlockStateHolder(Create.asResource("crimsite")))
                .addCatalystFluid(ResourceLocation.parse("lava"));

        vents.add(crimsite);

        ResourceVentHolder ochrum = new ResourceVentHolder("ochrum");

        ochrum.addGenerationState(new BlockStateHolder(Create.asResource("ochrum")))
                .addCatalystFluid(ResourceLocation.parse("lava"));

        vents.add(ochrum);

        ResourceVentHolder scorchia = new ResourceVentHolder("scorchia");

        scorchia.addGenerationState(new BlockStateHolder(Create.asResource("scorchia")))
                .addCatalystFluid(ResourceLocation.parse("lava"));

        vents.add(scorchia);

        ResourceVentHolder scoria = new ResourceVentHolder("scoria");

        scoria.addGenerationState(new BlockStateHolder(Create.asResource("scoria")))
                .addCatalystFluid(ResourceLocation.parse("lava"));

        vents.add(scoria);


    }

//    @SubscribeEvent
//    public static void configLoadListener(ModConfigEvent.Loading loadingEvent){
//        loadConfig(CreateResourceVents.CONFIG_PATH.toFile());
//    }
//
//    @SubscribeEvent
//    public static void configReloadListener(ModConfigEvent.Reloading loadingEvent){
//        loadConfig(CreateResourceVents.CONFIG_PATH.toFile());
//    }



    public static class ConfigSerializer implements JsonSerializer<Config>, JsonDeserializer<Config>{

        @Override
        public JsonElement serialize(Config src,
                                     Type type,
                                     JsonSerializationContext jsonSerializationContext
        ) {
            JsonObject config = new JsonObject();

            JsonArray vents = new JsonArray();

            for(ResourceVentHolder vent : src.vents){
                vents.add(jsonSerializationContext.serialize(vent, ResourceVentHolder.class));
            }

            config.add("vents", vents);

            return config;
        }

        @Override
        public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            Config c = new Config();

            JsonObject configJson = json.getAsJsonObject();

            JsonArray ventsArray = configJson.getAsJsonArray("vents");

            for(JsonElement e : ventsArray){
                c.vents.add(context.deserialize(e, ResourceVentHolder.class));
            }

            return c;
        }
    }

}
