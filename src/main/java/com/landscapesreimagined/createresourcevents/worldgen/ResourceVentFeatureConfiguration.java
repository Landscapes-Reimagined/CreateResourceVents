package com.landscapesreimagined.createresourcevents.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

public class ResourceVentFeatureConfiguration implements FeatureConfiguration {

    public static final Codec<ResourceVentFeatureConfiguration> CODEC = RecordCodecBuilder.create((fields) -> {

        return fields.group(Codec.INT.fieldOf("diameter").forGetter( (v) -> {
            return v.portionSize;
        }),
        Codec.INT.fieldOf("wallHeight").forGetter(  (v) -> {
            return v.riseAmount;
        }),
        Codec.BOOL.optionalFieldOf("clearArea", false).forGetter((v) ->{
            return v.makeSpaceInOven;
        }),
        IntProvider.codec(0, 3).fieldOf("maxColumnHeight").forGetter( (v) -> {
            return v.stiffPeakSize;
        }),
        BlockStateProvider.CODEC.fieldOf("baseBlock").forGetter( (v) ->{
            return v.solidIngredients;
        }),
        BlockStateProvider.CODEC.listOf().fieldOf("decorativeBlocks").forGetter( (v) ->{
            return v.sprinkles;
        }),
        BlockStateProvider.CODEC.fieldOf("vent").forGetter( (v) ->{
            return v.flavoring;
        }),
        BlockStateProvider.CODEC.fieldOf("liquidBlock").forGetter( (v) ->{
            return v.gooeyLiquidCenter;
        }),
        IntProvider.codec(1, 312).fieldOf("depth").forGetter( (v) -> {
            return v.depth;
        })
        ).apply(fields, ResourceVentFeatureConfiguration::new);

    });

    //above ground parameters
    private final Integer portionSize;
    private final Integer riseAmount;
    private final IntProvider stiffPeakSize;
    private final BlockStateProvider solidIngredients;
    private final List<BlockStateProvider> sprinkles;
    private final BlockStateProvider flavoring;
    private final Boolean makeSpaceInOven;

//    //below ground parameters
    private final BlockStateProvider gooeyLiquidCenter;
    private final IntProvider depth;



    public ResourceVentFeatureConfiguration(Integer diameter, Integer maxWallHeight, Boolean makeSpaceInOven, IntProvider maxColumnHeight, BlockStateProvider solidIngredients, List<BlockStateProvider> sprinkles, BlockStateProvider resourceVent, BlockStateProvider gooeyLiquidCenter, IntProvider depth) {
        this.portionSize = diameter;
        this.riseAmount = maxWallHeight;
        this.stiffPeakSize = maxColumnHeight;
        this.solidIngredients = solidIngredients;
        this.sprinkles = sprinkles;
        this.flavoring = resourceVent;
        this.gooeyLiquidCenter = gooeyLiquidCenter;
        this.depth = depth;
        this.makeSpaceInOven = makeSpaceInOven;
    }

    public IntProvider getDepth() {
        return depth;
    }

    public BlockStateProvider getGooeyLiquidCenter() {
        return gooeyLiquidCenter;
    }

    public BlockStateProvider getFlavoring() {
        return flavoring;
    }

    public List<BlockStateProvider> getSprinkles() {
        return sprinkles;
    }

    public BlockStateProvider getSolidIngredients() {
        return solidIngredients;
    }

    public IntProvider getStiffPeakSize() {
        return stiffPeakSize;
    }

    public int getRiseAmount() {
        return riseAmount;
    }

    public int getPortionSize() {
        return portionSize;
    }

    public Boolean getMakeSpaceInOven() {
        return makeSpaceInOven;
    }
}
