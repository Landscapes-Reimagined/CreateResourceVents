package com.landscapesreimagined.createresourcevents.mixin;

import com.landscapesreimagined.createresourcevents.PartialModels;
import com.landscapesreimagined.createresourcevents.SpriteShifts;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry.isDormantVentBlock;

@Mixin(value = BlazeBurnerRenderer.class)
public class BlazeBurnerRendererMixin {

    @Unique
    private static ThreadLocal<Boolean> createResourceVents$hasBlockBelow = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<BlazeBurnerBlock.HeatLevel> createResourceVents$heatLevel = ThreadLocal.withInitial(() -> BlazeBurnerBlock.HeatLevel.NONE);

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("HEAD"), remap = false)
    public void setBlockBelow(BlazeBurnerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay, CallbackInfo ci){
        createResourceVents$hasBlockBelow.set(isDormantVentBlock(be.getLevel(), be.getBlockPos()));
    }

    @Inject(method = "renderShared", at = @At(value = "HEAD"), remap = false)
    private static void setHeatLevel(PoseStack ms, PoseStack modelTransform, MultiBufferSource bufferSource, Level level, BlockState blockState, BlazeBurnerBlock.HeatLevel heatLevel, float animation, float horizontalAngle, boolean canDrawFlame, boolean drawGoggles, PartialModel drawHat, int hashCode, CallbackInfo ci){
//        createResourceVents$hasBlockBelow.set();
        createResourceVents$heatLevel.set(heatLevel);
    }


    @WrapOperation(method = "renderShared", at = @At(value = "FIELD", target = "Lcom/simibubi/create/AllPartialModels;BLAZE_BURNER_FLAME:Ldev/engine_room/flywheel/lib/model/baked/PartialModel;"), remap = false)
    private static PartialModel wrapGetFlamePartialModel(Operation<PartialModel> original){
        if( createResourceVents$hasBlockBelow.get() && createResourceVents$heatLevel.get().isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)){
            return PartialModels.IN_BURNER_FLAME;
        }else{
            return original.call();
        }
    }


//    @Inject(method = "animate", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerVisual;setupFlameInstance()V", shift = At.Shift.AFTER))
//    public void modifyAnimationSpeed(float partialTicks, CallbackInfo ci){
//        if(hasBlockUnder && this.heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING) && this.flame != null){
//            this.flame.speedU = -this.flame.speedU;
//            this.flame.speedV = -this.flame.speedV;
//        }
//    }

    @WrapOperation(
            method = "renderShared",
            at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/render/SuperByteBuffer;shiftUVScrolling(Lnet/createmod/catnip/render/SpriteShiftEntry;FF)Lnet/createmod/catnip/render/SuperByteBuffer;"),
            remap = false
    )
    private static SuperByteBuffer wrapShiftUVScrolling(SuperByteBuffer instance, SpriteShiftEntry spriteShiftEntry, float u, float v, Operation<SuperByteBuffer> original){

        if(createResourceVents$hasBlockBelow.get() && createResourceVents$heatLevel.get().isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)) {
            u = -u;
            v = -v;
        }


        return original.call(instance, spriteShiftEntry, u, v);
    }


    @WrapOperation(
            method = "renderShared",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/AllSpriteShifts;SUPER_BURNER_FLAME:Lnet/createmod/catnip/render/SpriteShiftEntry;"),
            remap = false
    )
    private static SpriteShiftEntry getOurSpriteShifter(Operation<SpriteShiftEntry> original){
        return createResourceVents$hasBlockBelow.get() ? SpriteShifts.SUPER_BURNER_FLAME : AllSpriteShifts.SUPER_BURNER_FLAME;
    }



}
