package com.landscapesreimagined.createresourcevents.Blocks;

import com.landscapesreimagined.createresourcevents.Blocks.BlockEntities.ModBlockEntities;
import com.landscapesreimagined.createresourcevents.Blocks.BlockEntities.ResourceVentBlockEntity;
import com.landscapesreimagined.createresourcevents.Blocks.BlockEntities.TickingBlockEntityTicker;
import com.landscapesreimagined.createresourcevents.Config.Config;
import com.landscapesreimagined.createresourcevents.Config.ResourceVentHolder;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ActiveResourceVentBlock extends Block implements IBE<ResourceVentBlockEntity> {

    public final List<BlockState> generationStates;

    /** will be null iff usesFluid */
    @UnknownNullability
    public final List<BlockState> reactantBlockStates;

    public final String ventName;

    /** will be null iff not usesFluid */
    @UnknownNullability
    public final List<Fluid> reactantFluids;

    public final boolean usesFluid;
    public int maxGenerationDistance;

    public ActiveResourceVentBlock(Properties pProperties, BlockState stateToGenerate, BlockState catalyst, String ventName) {
        this(pProperties, stateToGenerate, catalyst, 2, ventName);
    }

    public ActiveResourceVentBlock(Properties pProperties, BlockState stateToGenerate, Fluid catalyst, String ventName) {
        this(pProperties, stateToGenerate, catalyst, 2, ventName);
    }

    public ActiveResourceVentBlock(Properties pProperties, BlockState stateToGenerate, BlockState catalyst, int maxDist, String ventName) {
        this(pProperties, Collections.singletonList(stateToGenerate), Collections.singletonList(catalyst), null, maxDist, ventName);
    }

    public ActiveResourceVentBlock(Properties pProperties, BlockState stateToGenerate, Fluid catalyst, int maxDist, String ventName) {
        this(pProperties, Collections.singletonList(stateToGenerate), null, Collections.singletonList(catalyst), maxDist, ventName);
    }

    public ActiveResourceVentBlock(Properties pProperties, List<BlockState> statesToGenerate, List<BlockState> blockStateCatalysts, List<Fluid> fluidCatalysts, int maxDist, String ventName) {
        super(pProperties);

        this.ventName = ventName;
        this.generationStates = statesToGenerate;

        this.reactantFluids = fluidCatalysts;
        this.reactantBlockStates = blockStateCatalysts;
        this.usesFluid = (blockStateCatalysts == null);

        this.maxGenerationDistance = maxDist;

    }

    @Override
    public Class<ResourceVentBlockEntity> getBlockEntityClass() {
        return ResourceVentBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ResourceVentBlockEntity> getBlockEntityType() {
        return ModBlockEntities.RESOURCE_VENT.get();
    }

    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return new TickingBlockEntityTicker<>();
    }

    public void reload(Config newConfig){

        ResourceVentHolder updatedHolder = null;
        for(ResourceVentHolder candidate : newConfig.vents){
            if(Objects.equals(candidate.ventName, this.ventName)){
                updatedHolder = candidate;
                break;
            }
        }

        if(updatedHolder == null){
            return;
        }

        this.maxGenerationDistance = updatedHolder.maxGenerationDistance;

        this.generationStates.clear();

        this.generationStates.addAll(updatedHolder.buildGenerationStates());

        if(this.usesFluid && updatedHolder.usesFluid()){
            this.reactantFluids.clear();

            this.reactantFluids.addAll(updatedHolder.buildReactantFluids());
        }else if(!this.usesFluid && !updatedHolder.usesFluid()){
            this.reactantBlockStates.clear();

            this.reactantBlockStates.addAll( updatedHolder.buildReatantBlocks());
        }else{
            LogUtils.getLogger().error("Difference between updated vent reactant type and loaded vent reactant type! Not applying changes.");
        }
    }
}
