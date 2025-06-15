package com.landscapesreimagined.createresourcevents;

import com.simibubi.create.Create;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;

public class SpriteShifts {

    public static final SpriteShiftEntry SUPER_BURNER_FLAME =
            SpriteShifter.get(CreateResourceVents.asResource("block/blaze_burner_flame"),
                    CreateResourceVents.asResource("block/blaze_burner_flame_superheated_scroll"));

    public static void init(){}
}
