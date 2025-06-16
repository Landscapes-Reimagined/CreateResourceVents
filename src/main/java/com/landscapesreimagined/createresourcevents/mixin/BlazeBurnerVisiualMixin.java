package com.landscapesreimagined.createresourcevents.mixin;

import com.landscapesreimagined.createresourcevents.MixinStuff.VentTransformingBE;
import com.landscapesreimagined.createresourcevents.PartialModels;
import com.landscapesreimagined.createresourcevents.SpriteShifts;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerVisual;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Translate;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import net.createmod.catnip.render.SpriteShiftEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry.isDormantVentBlock;

@Mixin(value = BlazeBurnerVisual.class, remap = false)
public abstract class BlazeBurnerVisiualMixin extends AbstractBlockEntityVisual<BlazeBurnerBlockEntity> implements SimpleDynamicVisual, SimpleTickableVisual {

    @Shadow private @Nullable ScrollInstance flame;

    private boolean hadBlockUnderLastUpdate = false;
    private boolean hasBlockUnder = false;


    @Shadow private BlazeBurnerBlock.HeatLevel heatLevel;

    public BlazeBurnerVisiualMixin(VisualizationContext ctx, BlazeBurnerBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);
    }

    @Inject(method = "animate", at = @At("HEAD"))
    public void injectAnimate(float partialTicks, CallbackInfo ci){
        this.hasBlockUnder = isDormantVentBlock(this.level, this.pos);
        if(this.flame != null && this.hasBlockUnder && this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)){
            this.flame.position(getVisualPosition().getX(), getVisualPosition().getY() - 0.2f, getVisualPosition().getZ());

        }
    }

    private boolean isActivatingVent() {
        return ((VentTransformingBE) this.blockEntity).getConversionTime() != -1;
    }

    @Inject(method = "animate", at = @At("RETURN"))
    public void updateBlockUnderLastUpdate(float partialTicks, CallbackInfo ci){
        hadBlockUnderLastUpdate = hasBlockUnder;
    }

    @WrapOperation(method = "setupFlameInstance", at = @At(value = "FIELD", target = "Lcom/simibubi/create/AllPartialModels;BLAZE_BURNER_FLAME:Ldev/engine_room/flywheel/lib/model/baked/PartialModel;"))
    public PartialModel wrapGetFlamePartialModel(Operation<PartialModel> original){
        if(hasBlockUnder && this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)){
            return PartialModels.IN_BURNER_FLAME;
        }else{
            return original.call();
        }
    }


//    @Definition(id = "speed", local = @Local(type = float.class, ordinal = 2))
//    @Expression("speed")
//    @ModifyExpressionValue(method = "setupFlameInstance", at = @At("MIXINEXTRAS:EXPRESSION"))
//    public float modifyAnimationSpeed(float original){
//        return hasBlockUnder && this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING) ? -original : original;
//    }

    @Inject(method = "animate", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerVisual;setupFlameInstance()V", shift = At.Shift.AFTER))
    public void modifyAnimationSpeed(float partialTicks, CallbackInfo ci){
        if(hasBlockUnder && this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING) && this.flame != null){
            this.flame.speedU = -this.flame.speedU;
            this.flame.speedV = -this.flame.speedV;
        }
    }


    @WrapOperation(
            method = "setupFlameInstance",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/AllSpriteShifts;SUPER_BURNER_FLAME:Lnet/createmod/catnip/render/SpriteShiftEntry;")
    )
    public SpriteShiftEntry getOurSpriteShifter(Operation<SpriteShiftEntry> original){
        return hasBlockUnder ? SpriteShifts.SUPER_BURNER_FLAME : AllSpriteShifts.SUPER_BURNER_FLAME;
    }

//    @Definition(id = "animation", local = @Local(type = float.class, ordinal = 1))
//    @Expression("animation * .75")
//    @ModifyExpressionValue(method = "animate", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
//    public float wrapAnimationMul(float original){
//        return isDormantVentBlock(this.level, this.pos) && this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING) ? -original : original;
//    }

//    @WrapOperation(method = "animate", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/animation/LerpedFloat;getValue(F)F"))
//    public float flipAnimate(LerpedFloat instance, float partialTicks, Operation<Float> original){
//        if(isDormantVentBlock(this.level, this.pos) && this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
//            return original.call(instance, partialTicks) * -1;
//        }
//
//        return original.call(instance, partialTicks);
//
//    }

//    @Inject(method = "animate", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlockEntity;getHeatLevelForRender()Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;"), locals = LocalCapture.CAPTURE_FAILSOFT)
//    public void flipAnimateFrThisTime(float partialTicks, CallbackInfo ci, float animation, boolean validBlockAbove){
//        if(isDormantVentBlock(this.level, this.pos) && this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
//            animation *= -1;
//        }
//    }

    @WrapOperation(
            method = "animate",
            at = {
                    @At(value = "INVOKE", target = "Ldev/engine_room/flywheel/lib/instance/TransformedInstance;translateY(F)Ldev/engine_room/flywheel/lib/transform/Translate;", ordinal = 0),
                    @At(value = "INVOKE", target = "Ldev/engine_room/flywheel/lib/instance/TransformedInstance;translateY(F)Ldev/engine_room/flywheel/lib/transform/Translate;", ordinal = 1),
                    @At(value = "INVOKE", target = "Ldev/engine_room/flywheel/lib/instance/TransformedInstance;translateY(F)Ldev/engine_room/flywheel/lib/transform/Translate;", ordinal = 2)
            })
    public Translate flipAnimateOneLastTimeOMGGG(TransformedInstance instance, float v, Operation<Translate> original){
        if(isDormantVentBlock(this.level, this.pos) && this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
            return original.call(instance, -v);
        }else{
            return original.call(instance, v);
        }

    }

}
