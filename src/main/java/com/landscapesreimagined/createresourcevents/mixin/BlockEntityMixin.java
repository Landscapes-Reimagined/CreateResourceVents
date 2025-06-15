package com.landscapesreimagined.createresourcevents.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "loadStatic", at = @At("HEAD"), cancellable = true)
    private static void create_resource_vents$loadStatic(BlockPos pPos, BlockState pState, CompoundTag pTag, CallbackInfoReturnable<BlockEntity> cir){
        String id = pTag.getString("id");
        if(id.contains("molten_vents")){
            pTag.putString("id", "create_resource_vents:resource_vent_block_entity");
            cir.setReturnValue(BlockEntity.loadStatic(pPos, pState, pTag));
        }
    }
}
