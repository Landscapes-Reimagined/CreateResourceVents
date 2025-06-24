package com.landscapesreimagined.createresourcevents.Blocks.BlockEntities;

import com.landscapesreimagined.createresourcevents.Blocks.ActiveResourceVentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class ResourceVentBlockEntity extends TickingBlockEntity {

    public ResourceVentBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void tick(){

        final int maxGenerationDistance = getMaxGenerationDistanceFromBlock();

        BlockPos pos = this.getBlockPos();

        for(int dx = -maxGenerationDistance; dx <= maxGenerationDistance; dx++){
            for(int dy = -maxGenerationDistance; dy <= maxGenerationDistance; dy++){

                int twoDManhattan = Math.abs(dx) + Math.abs(dy);

                if(twoDManhattan > maxGenerationDistance) continue;

                for(int dz = -maxGenerationDistance; dz <= maxGenerationDistance; dz++){

                    int threeDManhattan = twoDManhattan + Math.abs(dz);

                    if(threeDManhattan > maxGenerationDistance){
                        continue;
                    }

                    checkAndConvertLocation(pos.offset(dx, dy, dz));
                }
            }
        }

    }

    private void checkAndConvertLocation(BlockPos offset) {

        //constants
        List<BlockState> generationStates = getGenerationStatesFromBlock();
        Predicate<BlockState> statePredicate = getBlockStateCheckPredicate();

        if (this.level == null || statePredicate == null){
            return;
        }

        //check blockstate
        BlockState blockState = level.getBlockState(offset);
        if(!statePredicate.test(blockState)) return;

        //convert fluid to block
        //todo: enumerate the different behaviours for setting block states from the list
        if(!this.level.isClientSide && generationStates.get(0) != null) {
            level.setBlock(offset, generationStates.get(0), Block.UPDATE_ALL);
            level.playSound(null, offset,
                    SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS,
                    1.0f, 1.0f
            );

            ((ServerLevel) this.level).sendParticles(ParticleTypes.LARGE_SMOKE,
                    offset.getX() + 0.5D, offset.getY() + 0.25D, offset.getZ() + 0.5D,
                    8,
                    0.5D, 0.25D, 0.5D, 0.0D
            );
        }
    }


    //light abstraction of checking blockstates to keep function looking nice
    @Nullable
    private Predicate<BlockState> getBlockStateCheckPredicate(){
        ActiveResourceVentBlock block = ensureValidBlockState();
        if(block == null) return null;

        //fluid state check
        if(block.usesFluid){
            return ((state) -> {
                FluidState fluidState = state.getFluidState();

                if(fluidState.isEmpty() || !fluidState.isSource()){
                    return false;
                }

                return block.reactantFluids.stream().anyMatch(
                        fluidState::is
                );
            });
        }else{
            //block state check
            return ((state) ->
                block.reactantBlockStates.stream().anyMatch(
                    (reactantState) -> {


                        var stateCheckProperties = reactantState.getProperties();
                        var nonStateCheckProperties = reactantState.getBlock().defaultBlockState();

                        return stateCheckProperties.stream().allMatch((property ) -> {
                            if(!state.hasProperty(property)) return false;
                            return reactantState.getValue(property).equals(state.getValue(property));
                        }) && reactantState.is(state.getBlock());
                    }
            ));
        }

    }

    public List<BlockState> getGenerationStatesFromBlock(){
        ActiveResourceVentBlock activeVentBlock = ensureValidBlockState();
        if (activeVentBlock == null) return null;

        return activeVentBlock.generationStates;
    }

    public int getMaxGenerationDistanceFromBlock(){
        ActiveResourceVentBlock activeVentBlock = ensureValidBlockState();
        if(activeVentBlock == null) return -1;

        return activeVentBlock.maxGenerationDistance;
    }

    private @Nullable ActiveResourceVentBlock ensureValidBlockState() {
        if(this.level == null)
            return null;

        BlockState activeVentState = this.level.getBlockState(this.getBlockPos());

        if(!(activeVentState.getBlock() instanceof ActiveResourceVentBlock activeVentBlock))
            return null;
        return activeVentBlock;
    }

}
