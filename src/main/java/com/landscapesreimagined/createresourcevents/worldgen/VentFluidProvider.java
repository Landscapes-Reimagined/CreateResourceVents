package com.landscapesreimagined.createresourcevents.worldgen;

import com.landscapesreimagined.createresourcevents.Blocks.ActiveResourceVentBlock;
import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.landscapesreimagined.createresourcevents.Config.Config;
import com.landscapesreimagined.createresourcevents.Config.ResourceVentHolder;
import com.landscapesreimagined.createresourcevents.CreateResourceVents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VentFluidProvider extends BlockStateProvider {
    public static final Codec<VentFluidProvider> CODEC = RecordCodecBuilder.create(
            (instance) -> {
                return instance.group(ResourceLocation.CODEC.fieldOf("vent").forGetter(
                        (vars) -> vars.ventID
                )).apply(instance, VentFluidProvider::new);
            }
    );

    private final ResourceLocation ventID;

    public VentFluidProvider(ResourceLocation ventID) {
        this.ventID = ventID;
    }

    @Override
    protected BlockStateProviderType<?> type() {
        return null;
    }

    @Override
    public @NotNull BlockState getState(@NotNull RandomSource pRandom, @NotNull BlockPos pPos) {
        for(ResourceVentHolder holder : Config.INSTANCE.vents){
            if(holder.ventName.equals(ventID.getPath())){
                List<Fluid> fluids = holder.buildReactantFluids();
                Fluid sampledFluid = fluids.get(pRandom.nextIntBetweenInclusive(0, fluids.size() - 1));
                return sampledFluid.defaultFluidState().createLegacyBlock();
            }
        }

        return Blocks.AIR.defaultBlockState();
    }

}
