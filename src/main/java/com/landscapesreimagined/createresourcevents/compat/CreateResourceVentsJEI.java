package com.landscapesreimagined.createresourcevents.compat;


import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.landscapesreimagined.createresourcevents.Config.Config;
import com.landscapesreimagined.createresourcevents.Config.ResourceVentHolder;
import com.landscapesreimagined.createresourcevents.CreateResourceVents;
import com.landscapesreimagined.createresourcevents.compat.funkyRecipeHacks.ConversionRecipe;
import com.landscapesreimagined.createresourcevents.compat.funkyRecipeHacks.ConversionRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class CreateResourceVentsJEI implements IModPlugin {

    public static RecipeType<ConversionRecipe> CONVERSION_RECIPE_TYPE = RecipeType.create("create_resource_vents", "vent_conversion", ConversionRecipe.class);


    @Override
    public ResourceLocation getPluginUid() {
        return CreateResourceVents.asResource("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        System.out.println("hello :D");
        registration.addRecipeCategories(new ConversionRecipeCategory());
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        System.out.println("hello :D");

//        registration.addRecipes(CONVERSION_RECIPE_TYPE, List.of(new ConversionRecipe(Config.INSTANCE.getVent("crimsite"))))

        ArrayList<ConversionRecipe> ventRecipies = new ArrayList<>();
        for (ResourceVentHolder vent : Config.INSTANCE.vents) {
            var recipe = new ConversionRecipe(vent);

            ventRecipies.add(recipe);
            Config.INSTANCE.reloadListeners.add(recipe::reload);
        }

        registration.addRecipes(CONVERSION_RECIPE_TYPE, ventRecipies);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        for(ResourceVentHolder holder : Config.INSTANCE.vents){
            registration.addRecipeCatalyst(DynamicRegistry.getActiveVent(holder.ventName), CONVERSION_RECIPE_TYPE);
        }
    }
}
