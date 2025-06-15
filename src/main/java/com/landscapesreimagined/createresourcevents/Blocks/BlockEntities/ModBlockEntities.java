package com.landscapesreimagined.createresourcevents.Blocks.BlockEntities;

import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.landscapesreimagined.createresourcevents.CreateResourceVents;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;

@SuppressWarnings("unchecked")
public class ModBlockEntities {

    public static final BlockEntityEntry<ResourceVentBlockEntity> RESOURCE_VENT = CreateResourceVents.RESOURCE_VENTS_REGISTRATE
            .blockEntity("resource_vent_block_entity", ResourceVentBlockEntity::new)
            .validBlocks(
                    DynamicRegistry.DORMANT_ACTIVE_MAP
                            .values()
                            .toArray(BlockEntry[]::new)
            )
            .register();


    public static void register() {};

}
