package com.landscapesreimagined.createresourcevents.mixin;

import com.landscapesreimagined.createresourcevents.Config.Config;
import com.landscapesreimagined.createresourcevents.CreateResourceVents;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "reloadResources", at = @At("HEAD"))
    public void createresourcevents$reloadResources(Collection<String> p_129862_, CallbackInfoReturnable<CompletableFuture<Void>> cir){

        Config.loadConfig(CreateResourceVents.CONFIG_PATH.toFile());
    }

}
