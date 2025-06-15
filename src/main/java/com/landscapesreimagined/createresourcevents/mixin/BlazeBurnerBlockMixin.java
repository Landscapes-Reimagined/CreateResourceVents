package com.landscapesreimagined.createresourcevents.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.flywheel.FlywheelVisual;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry.isDormantVentBlock;

@Mixin(value = BlazeBurnerBlock.class, remap = false)
public abstract class BlazeBurnerBlockMixin extends HorizontalDirectionalBlock implements IBE<BlazeBurnerBlockEntity>, IWrenchable {

    private static BooleanProperty OPEN = BlockStateProperties.OPEN;

    protected BlazeBurnerBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @WrapOperation(method = "createBlockStateDefinition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"))
    public StateDefinition.Builder<Block, BlockState> addStateForBlazeBurner(StateDefinition.Builder<Block, BlockState> instance, Property<?>[] properties, Operation<StateDefinition.Builder<Block, BlockState>> original){

        return original.call(instance, properties).add(OPEN);
    }

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            remap = false)
    public BlockState wrapDefaultBlockState(BlazeBurnerBlock instance, Operation<BlockState> original){

        return original.call(instance).setValue(OPEN, false);
    }

    @WrapOperation(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"))
    public BlockState wrapPlacementBlockState(BlazeBurnerBlock instance, Operation<BlockState> original, BlockPlaceContext ctx){

        if(isDormantVentBlock(ctx.getLevel(), ctx.getClickedPos())){
            return original.call(instance).setValue(OPEN, true);
        }

        return original.call(instance);

    }



    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        pLevel.scheduleTick(pCurrentPos, this, 2);

//        if(pLevel.isClientSide() && VisualizationManager.supportsVisualization(pLevel)) {
//            VisualizationManager.get(pLevel).blockEntities().queueUpdate();
//        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

        if (isDormantVentBlock(pLevel, pPos) && !pState.getValue(OPEN)) {
            pLevel.setBlock(pPos, pState.setValue(OPEN, true), 3);

        }else if( !isDormantVentBlock(pLevel, pPos) && pState.getValue(OPEN)){
            pLevel.setBlock(pPos, pState.setValue(OPEN, false), 3);

        }
    }



}
