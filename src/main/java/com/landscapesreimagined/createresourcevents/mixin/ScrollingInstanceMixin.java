package com.landscapesreimagined.createresourcevents.mixin;


import com.landscapesreimagined.createresourcevents.MixinStuff.FlippingTrackingInstance;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ScrollInstance.class)
public class ScrollingInstanceMixin implements FlippingTrackingInstance {
    public boolean hasRotated = false;


    @Override
    public boolean wasRotated() {
        return hasRotated;
    }

    @Override
    public void rotate() {
        hasRotated =! hasRotated;
    }
}
