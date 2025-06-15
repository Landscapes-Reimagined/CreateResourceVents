package com.landscapesreimagined.createresourcevents.Blocks;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;

public class DormantVentBlock extends Block {

    public final BlockEntry<ActiveResourceVentBlock> activeVent;

    public DormantVentBlock(Properties properties, BlockEntry<ActiveResourceVentBlock> activeBlock) {
		super(properties);

        this.activeVent = activeBlock;
    }
}
