package com.landscapesreimagined.createresourcevents.mixin;


import com.landscapesreimagined.createresourcevents.Blocks.DormantVentBlock;
import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.landscapesreimagined.createresourcevents.MixinStuff.VentTransformingBE;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry.isDormantVentBlock;

@Mixin(value = BlazeBurnerBlockEntity.class, remap = false)
public abstract class BlazeBurnerBlockEntityMixin extends SmartBlockEntity implements VentTransformingBE {

    @Shadow public abstract BlazeBurnerBlock.HeatLevel getHeatLevelFromBlock();

    @Shadow protected abstract BlazeBurnerBlock.HeatLevel getHeatLevel();

    @Unique
    int conversionTime = -1;

    public BlazeBurnerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "write", at = @At("HEAD"))
    public void writeMixin(CompoundTag compound, boolean clientPacket, CallbackInfo ci){
        compound.putInt("dormantVentConversionTime", conversionTime);
    }

    @Inject(method = "read", at = @At("HEAD"))
    public void readMixin(CompoundTag compound, boolean clientPacket, CallbackInfo ci){
        this.conversionTime = compound.getInt("dormantVentConversionTime");
    }


    @Inject(method = "tick", at = @At("HEAD"))
    public void tickMixin(CallbackInfo ci){
        if(!this.hasLevel()){
            return;
        }

        Block possibleVent = this.level.getBlockState(this.getBlockPos().below()).getBlock();

        if(
                DynamicRegistry.DORMANT_ACTIVE_MAP.keySet()
                        .stream().anyMatch(
                                (e)
                                        -> e.get() == possibleVent
                        ) &&
                this.getHeatLevelFromBlock().isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)
        ){
            if(conversionTime == -1 ){
                conversionTime = 200;
            }if(conversionTime == 0){
                BlockEntry<DormantVentBlock> jesusChrist = DynamicRegistry.DORMANT_ACTIVE_MAP.keySet()
                        .stream().filter((v) ->
                        v.get() == this.level.getBlockState(this.getBlockPos().below()).getBlock()
                ).findFirst().get();
                this.level.setBlock(this.getBlockPos().below(), DynamicRegistry.DORMANT_ACTIVE_MAP.get(jesusChrist).getDefaultState(), 3);
                this.conversionTime = -1;
            }else{
                this.conversionTime -= 1;
            }

        }else{
            this.conversionTime = -1;
        }
    }

    @Override
    public int getConversionTime() {
        return this.conversionTime;
    }

    @Inject(method = "isValidBlockAbove", cancellable = true, at = @At("HEAD"))
    public void injectValidBlockAbove(CallbackInfoReturnable<Boolean> cir){
        if (!isVirtual() && isDormantVentBlock(this.getLevel(), this.getBlockPos()) && this.getHeatLevelFromBlock().isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "spawnParticles", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getRandom()Lnet/minecraft/util/RandomSource;"))
    public void noSpawnParticlesIfConverting(BlazeBurnerBlock.HeatLevel heatLevel, double burstMult, CallbackInfo ci){
        if(heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING) &&
                isDormantVentBlock(this.getLevel(), this.getBlockPos()) &&
                getLevel().random.nextIntBetweenInclusive(1, 3) == 1){
            ci.cancel();
        }
    }

    @WrapOperation(method = "spawnParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 0))
    public void dontSpawnSmokeParticles(Level instance, ParticleOptions pParticleData, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Operation<Void> original){
        if(!this.getHeatLevelFromBlock().isAtLeast(BlazeBurnerBlock.HeatLevel.SEETHING)){
            original.call(instance, pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}
