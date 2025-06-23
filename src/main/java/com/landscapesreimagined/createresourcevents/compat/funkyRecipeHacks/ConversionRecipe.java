package com.landscapesreimagined.createresourcevents.compat.funkyRecipeHacks;

import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.landscapesreimagined.createresourcevents.Config.Config;
import com.landscapesreimagined.createresourcevents.Config.ResourceVentHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ConversionRecipe {

    private ResourceVentHolder holder;

    public ConversionRecipe(ResourceVentHolder vent){
        this.holder = vent;
    }

    public void reload(Config newConfig){
        ResourceVentHolder updatedHolder = null;
        for(ResourceVentHolder candidate : newConfig.vents){
            if(Objects.equals(candidate.ventName, this.holder.ventName)){
                updatedHolder = candidate;
                break;
            }
        }

        this.holder = updatedHolder;
    }


    public List<@Nullable ItemStack> getReactantBlocks(){
        if(this.holder.usesFluid()){
            return Collections.emptyList();
        }

        return this.holder.buildReatantBlocks()
                .stream()
                .map((state) -> state
                        .getBlock()
                        .asItem()
                        .getDefaultInstance())
                .toList();
    }

    public boolean usesFluid(){
        return this.holder.usesFluid();
    }

    public List<@Nullable FluidStack> getReactantFluids() {

        if(!this.holder.usesFluid()){
            return Collections.emptyList();
        }

        return this.holder.buildReactantFluids()
                .stream()
                .map((f) -> new FluidStack(f, 1000))
                .toList();
    }

    public ItemStack getVentCatalyst() {
        return DynamicRegistry.getActiveVent(this.holder.ventName).asStack();
    }

    public BlockState getVentState(){
        return DynamicRegistry.getActiveVent(this.holder.ventName).getDefaultState();
    }

    public List<@Nullable ItemStack> getOutputBlocks() {

        return this.holder.buildGenerationStates()
                .stream()
                .map((state) -> state
                        .getBlock()
                        .asItem()
                        .getDefaultInstance())
                .toList();

    }

}
