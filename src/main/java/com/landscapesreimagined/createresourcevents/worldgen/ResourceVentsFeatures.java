package com.landscapesreimagined.createresourcevents.worldgen;

import com.landscapesreimagined.createresourcevents.CreateResourceVents;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ResourceVentsFeatures {
    public static final DeferredRegister<BlockStateProviderType<?>> PROVIDER_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES, CreateResourceVents.MODID);
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE_REGISTER = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, CreateResourceVents.MODID);

    public static final RegistryObject<PlacementModifierType<BiomePredicatePlacementModifier>> PREDICATE_MODIFIER_TYPE = PLACEMENT_MODIFIER_TYPE_REGISTER.register("biome_switch", () -> new PlacementModifierType<BiomePredicatePlacementModifier>() {
        @Override
        public Codec<BiomePredicatePlacementModifier> codec() {
            return BiomePredicatePlacementModifier.CODEC;
        }
    });

    public static final RegistryObject<BlockStateProviderType<StateDataProvider>> STATE_DATA_PROVIDER_TYPE = PROVIDER_TYPE_REGISTER.register("state_data_provider", () -> new BlockStateProviderType<>(StateDataProvider.CODEC));
    public static final RegistryObject<BlockStateProviderType<VentFluidProvider>> VENT_FLUID_PROVIDER_TYPE = PROVIDER_TYPE_REGISTER.register("vent_fluid", () -> new BlockStateProviderType<>(VentFluidProvider.CODEC));

    public static final DeferredRegister<Feature<?>> FEATURE_REGISTER = DeferredRegister.create(ForgeRegistries.FEATURES, CreateResourceVents.MODID);

    public static final RegistryObject<Feature<ResourceVentFeatureConfiguration>> RESOURCE_VENT =
            FEATURE_REGISTER.register("resource_vent", () ->
                    new ResourceVentFeature(ResourceVentFeatureConfiguration.CODEC)
            );

    public static void register(IEventBus eventBus) {
        PROVIDER_TYPE_REGISTER.register(eventBus);
        PLACEMENT_MODIFIER_TYPE_REGISTER.register(eventBus);
        FEATURE_REGISTER.register(eventBus);

    }


}
