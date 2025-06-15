package com.landscapesreimagined.createresourcevents;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ItemEvent;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Objects;

public class ResourceVentCreativeTab extends CreativeModeTab {

    private long prev = System.currentTimeMillis();
    private ArrayDeque<RegistryEntry<Item>> scrollingTabItems = null;

    public ResourceVentCreativeTab(Builder builder) {
        super(builder);
    }

    @Override
    public ItemStack getIconItem() {//this bit in particular makes the icon scroll/change between the specified items, or registered dinosaur display cases

        if(scrollingTabItems == null){
            scrollingTabItems = new ArrayDeque<>(
                    CreateResourceVents.RESOURCE_VENTS_REGISTRATE.getAll(Registries.ITEM).stream().toList()
            );
        }

        if(System.currentTimeMillis() >= prev + 2500){
            RegistryEntry<Item> i = scrollingTabItems.poll();
            scrollingTabItems.addLast(i);
            prev = System.currentTimeMillis();
        }
//                    SCROLLING_TAB_ITEMS.get(name).addLast(i);
        return scrollingTabItems.peek().get().getDefaultInstance();



    }

    @Override
    public ResourceLocation getBackgroundLocation() {
        return ResourceLocation.parse("textures/gui/container/creative_inventory/tab_items.png");
    }

//    @Override
//    public @NotNull ResourceLocation getTabsImage() {
//        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this.getIconItem().getItem()));
//    }
}
