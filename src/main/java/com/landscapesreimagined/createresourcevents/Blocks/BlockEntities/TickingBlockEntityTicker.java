package com.landscapesreimagined.createresourcevents.Blocks.BlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

public class TickingBlockEntityTicker<B extends BlockEntity> implements BlockEntityTicker<B> {

    @Override
    @ParametersAreNonnullByDefault
    public void tick(Level pLevel, BlockPos pPos, BlockState pState, B pBlockEntity) {
        if (!pBlockEntity.hasLevel())
            pBlockEntity.setLevel(pLevel);
        ((TickingBlockEntity) pBlockEntity).tick();
    }

}
