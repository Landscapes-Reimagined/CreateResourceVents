package com.landscapesreimagined.createresourcevents.mixin;

import net.minecraft.world.level.block.state.StateHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StateHolder.class)
public interface HolderOwnerAccessor<O> {

    @Accessor(value = "owner")
    public O getOwner();
}
