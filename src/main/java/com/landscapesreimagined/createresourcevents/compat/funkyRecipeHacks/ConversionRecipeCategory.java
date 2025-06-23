package com.landscapesreimagined.createresourcevents.compat.funkyRecipeHacks;

import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.landscapesreimagined.createresourcevents.compat.CreateResourceVentsJEI;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethodStage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Optional;

public class ConversionRecipeCategory implements IRecipeCategory<ConversionRecipe> {
    @Override
    public @NotNull RecipeType<ConversionRecipe> getRecipeType() {
        return CreateResourceVentsJEI.CONVERSION_RECIPE_TYPE;
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("create_resource_vents.jei.conversion_category_title");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return new ItemIcon(() -> DynamicRegistry.getActiveVent("crimsite") == null ? DynamicRegistry.ACTIVE_MAP.values().stream().findFirst().get().asStack() : DynamicRegistry.getActiveVent("crimsite").asStack());
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ConversionRecipe recipe, @NotNull IFocusGroup focuses) {
//        builder.addInputSlot().addIngredients
        if(recipe.usesFluid()){
            builder.addInputSlot(0, 5).addIngredients(ForgeTypes.FLUID_STACK, recipe.getReactantFluids()).setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1);
        }else{
            builder.addInputSlot(0, 5).addIngredients(VanillaTypes.ITEM_STACK, recipe.getReactantBlocks()).setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1);
        }


        builder.addSlot(RecipeIngredientRole.CATALYST, 64-8, 50-16).addIngredients(VanillaTypes.ITEM_STACK, List.of(recipe.getVentCatalyst())).setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1);
        builder.addOutputSlot(100-16, 5).addIngredients(VanillaTypes.ITEM_STACK, recipe.getOutputBlocks()).setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1);
    }

    @Override
    public void draw(@NotNull ConversionRecipe recipe, IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {

        PoseStack matrixStack = graphics.pose();

        int shadowBlockDX = -38;
        int shadowBlockDY = -30;

        AllGuiTextures.JEI_SHADOW.render(graphics, 62 + shadowBlockDX, 47 + shadowBlockDY);
        matrixStack.pushPose();
        matrixStack.mulPose(Axis.ZN.rotationDegrees(-180));
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, -52, -45);
        matrixStack.popPose();


        BlockState state = recipe.getVentState();

//        PoseStack matrixStack = graphics.pose();

        matrixStack.pushPose();
        matrixStack.translate(74 + shadowBlockDX, 51 + shadowBlockDY, 100);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 20;

        GuiGameElement.of(state)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .scale(scale)
                .render(graphics);

        matrixStack.popPose();
    }
}
