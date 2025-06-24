package com.landscapesreimagined.createresourcevents.Blocks;

import com.landscapesreimagined.createresourcevents.Config.Config;
import com.landscapesreimagined.createresourcevents.Config.ResourceVentHolder;
import com.landscapesreimagined.createresourcevents.CreateResourceVents;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;

public class DynamicRegistry {

    public static HashMap<BlockEntry<DormantVentBlock>, BlockEntry<ActiveResourceVentBlock>>
            DORMANT_ACTIVE_MAP = new HashMap<>();

    public static HashMap<ResourceLocation, BlockEntry<ActiveResourceVentBlock>> ACTIVE_MAP = new HashMap<>();

    public static boolean isDormantVentBlock(Level level, BlockPos pos) {

        if(level == null) return false;

        return ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos.below()).getBlock()).toString().contains("vents:dormant");
    }

    public static BlockEntry<ActiveResourceVentBlock> getActiveVent(String ventName){
        return ACTIVE_MAP.get(CreateResourceVents.asResource(ventName));
    }

//    public static ArrayList<ItemEntry<BlockItem>> BLOCK_ITEMS = new ArrayList<>();

    public static void register() {

        CreateResourceVents.RESOURCE_VENTS_REGISTRATE.setCreativeTab(CreateResourceVents.BASE_CREATIVE_TAB);

        for(ResourceVentHolder holder : Config.INSTANCE.vents){



            BlockEntry<ActiveResourceVentBlock> activeVentEntry = CreateResourceVents.RESOURCE_VENTS_REGISTRATE
                    .block("active_" + holder.ventName + "_vent",
                            (callback) -> holder.build(
                                BlockBehaviour.Properties.copy(Blocks.TUFF)
                                    .explosionResistance(1200)
                                    .sound(SoundType.TUFF)
                                    .lightLevel((state) -> 15)
                    ))
                    .loot((lootTables, a) -> lootTables.dropOther(a, Items.AIR))
                    .item().model((thing, prov) -> prov.blockItem(thing)).build()
                    .register();

//            ItemEntry<BlockItem> activeVentItem = CreateResourceVents.RESOURCE_VENTS_REGISTRATE.block

            BlockEntry<DormantVentBlock> dormantVentEntry = CreateResourceVents.RESOURCE_VENTS_REGISTRATE
                    .block("dormant_" + holder.ventName + "_vent",
                            (callback) -> new DormantVentBlock(
                                    BlockBehaviour.Properties.copy(Blocks.TUFF)
                                            .explosionResistance(1200)
                                            .sound(SoundType.TUFF),
                                    activeVentEntry
                            )
                    )
                    .loot((lootTables, a) -> lootTables.dropOther(a, Items.AIR))
                    .item().model((thing, prov) -> prov.blockItem(thing)).build()
                    .register();

            DORMANT_ACTIVE_MAP.put(dormantVentEntry, activeVentEntry);
            ACTIVE_MAP.put(CreateResourceVents.asResource(holder.ventName), activeVentEntry);






        }

    };
}
