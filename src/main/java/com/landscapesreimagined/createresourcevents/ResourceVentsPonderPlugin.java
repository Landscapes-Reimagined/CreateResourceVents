package com.landscapesreimagined.createresourcevents;

import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.simibubi.create.Create;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import static com.landscapesreimagined.createresourcevents.CreateResourceVents.RESOURCE_VENTS_REGISTRATE;

public class ResourceVentsPonderPlugin implements PonderPlugin {

    public static final ResourceLocation RESOURCE_VENTS_PONDERS = CreateResourceVents.asResource("resource_vents_ponders");

    @Override
    public String getModId() {
        return CreateResourceVents.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
//        PonderPlugin.super.registerScenes(helper);
        ResourceVentsPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
//        PonderPlugin.super.registerTags(helper);

        helper.registerTag(RESOURCE_VENTS_PONDERS)
//                .icon(CreateResourceVents.asResource("dormant_crimsite_vent"))
                .item(DynamicRegistry.getActiveVent("crimsite") == null ? DynamicRegistry.ACTIVE_MAP.values().stream().findFirst().get(): DynamicRegistry.getActiveVent("crimsite"))

                .title("Resource Vents")
                .description("All ponders for Create: Resource Vents")
                .addToIndex()
                .register();

        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);


        RESOURCE_VENTS_REGISTRATE.getAll(Registries.BLOCK).forEach((a) -> {
            HELPER.addToTag(RESOURCE_VENTS_PONDERS).add(a);
        });
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
//        PonderPlugin.super.registerSharedText(helper);
    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {
//        PonderPlugin.super.onPonderLevelRestore(ponderLevel);
    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
//        PonderPlugin.super.indexExclusions(helper);
    }
}
