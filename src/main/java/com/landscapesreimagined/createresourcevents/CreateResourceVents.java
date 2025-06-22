package com.landscapesreimagined.createresourcevents;

import com.landscapesreimagined.createresourcevents.Blocks.BlockEntities.ModBlockEntities;
import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.landscapesreimagined.createresourcevents.Config.Config;
import com.landscapesreimagined.createresourcevents.worldgen.ResourceVentsFeatures;
import com.mojang.logging.LogUtils;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.nio.file.Path;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateResourceVents.MODID)
public class CreateResourceVents {

    //todo: allow null biome tag
    //todo: futher testing of the biome switch
    //todo: test Create: copycat+ copycats
    //todo: JEI integration
    //todo: finish last ponder

    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_resource_vents";

    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateResourceVents.MODID);

    public static RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB;
    public static final CreateRegistrate RESOURCE_VENTS_REGISTRATE = CreateRegistrate.create(MODID);

    public static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("create_resource_vents.json");


    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "create_resource_vents" namespace


    static {
        BASE_CREATIVE_TAB = REGISTER.register("base",
                () -> new ResourceVentCreativeTab(
                        CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.create_resource_vents.base"))
                        .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                        .icon(Items.LAVA_BUCKET::getDefaultInstance)
                        .displayItems((params, output) -> {
                            output.acceptAll(
                                    RESOURCE_VENTS_REGISTRATE.getAll(Registries.BLOCK).stream().map((entry) -> entry.get().asItem().getDefaultInstance()).toList()                            );
                        })
                ));
    }

    public CreateResourceVents() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        RESOURCE_VENTS_REGISTRATE.registerEventListeners(modEventBus);

        Config.loadConfig(CreateResourceVents.CONFIG_PATH.toFile());

        REGISTER.register(modEventBus);

        ResourceVentsFeatures.register(modEventBus);



        DynamicRegistry.register();
        ModBlockEntities.register();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.onCtorClient(modEventBus));



        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        MinecraftForge.EVENT_BUS.register(Config.class);
        modEventBus.register(Config.class);
    }

    public static ResourceLocation asResource(String path){
        return ResourceLocation.parse(MODID + ":" + path);
    }


}
