package com.landscapesreimagined.createresourcevents;

import com.landscapesreimagined.createresourcevents.Blocks.DynamicRegistry;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.ParticleEmitter;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ResourceVentsPonderScenes {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper){

        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);


        HELPER.forComponents(DynamicRegistry.DORMANT_ACTIVE_MAP.keySet())
                .addStoryBoard("vent_activation_ponder", ResourceVentsPonderScenes::activateVentPonder, ResourceVentsPonderPlugin.RESOURCE_VENTS_PONDERS);
        HELPER.forComponents(DynamicRegistry.DORMANT_ACTIVE_MAP.values())
                .addStoryBoard("vent_automation_ponder", ResourceVentsPonderScenes::ventGenerationPonder, ResourceVentsPonderPlugin.RESOURCE_VENTS_PONDERS);

    }


    public static void activateVentPonder(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("activate_vent", "Activate Resource Vents");
        scene.showBasePlate();
        scene.idle(15);
        scene.world().showSection(util.select().position(new BlockPos(2, 1, 2)), Direction.DOWN);
        scene.idle(20);
        scene.addKeyframe();
        BlockPos ventPos = new BlockPos(2, 1, 2);

        scene.overlay().showText(100)
                .placeNearTarget()
                .text("This is a Dormant Resource Vent. To generate blocks with the Vent, you must activate it.")
                .pointAt(util.vector().centerOf(ventPos));

        scene.idle(100);

        scene.addKeyframe();
        scene.idle(10);
        scene.overlay().showText(75)
                .placeNearTarget()
                .text("To activate the Dormant Vent, place a blaze burner above it")
                .pointAt(util.vector().centerOf(ventPos));
        scene.idle(85);
        scene.world().showSection(util.select().position(ventPos.above()), Direction.DOWN);
        scene.idle(20);

        scene.addKeyframe();

        scene.overlay().showText(100)
                .placeNearTarget()
                .text("Then, fuel the Blaze Burner with a Blaze Cake or any other fuel that Superheats the blaze burner.")
                        .pointAt(util.vector().centerOf(ventPos.above()));
        scene.idle(100);
        ItemStack blazeCake = AllItems.BLAZE_CAKE.asStack();
        scene.overlay().showControls(util.vector().topOf(ventPos.above()), Pointing.DOWN, 20).rightClick().withItem(blazeCake);
        scene.idle(5);
        scene.world().modifyBlock(ventPos.above(), (state) -> state.setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SEETHING), false);
        scene.idle(15);
        scene.addKeyframe();
        scene.overlay().showText(125)
                .placeNearTarget()
                .text("After 10 seconds the Dormant Vent will convert into an Active Resource Vent.")
                .pointAt(util.vector().centerOf(ventPos));
        scene.idle(100);
        scene.world().setBlock(ventPos, DynamicRegistry.getActiveVent("crimsite").getDefaultState(), false);
        scene.idle(50);


    }

    public static void ventGenerationPonder(SceneBuilder builder, SceneBuildingUtil util){
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);

        scene.title("automate_vent", "Automate Resource Vents");

        BlockPos ventPos = new BlockPos(2, 1, 2);
        BlockPos pumpPos = new BlockPos(2, 1, 4);

        Selection pipe = util.select().fromTo(new BlockPos(5, 1, 5), new BlockPos(2, 1, 5));

        Selection tank = util.select().cuboid(new BlockPos(6, 0, 3), new Vec3i(-1, 3, 1));


        Selection kenetics = util.select().fromTo(1, 1, 4, 1, 1, 5);



//        Selection kenetics = stuff.add(util.select().position(pumpPos));

        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(15);
        scene.world().showSection(util.select().position(new BlockPos(2, 1, 2)), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .text("This is an Active Resource Vent.")
                .pointAt(util.vector().centerOf(ventPos));

        scene.idle(55);

        scene.overlay().showText(90)
                .attachKeyFrame()
                .placeNearTarget()

                .text("To automate a resource stone with a Resource Vent, supply the vent with it's corresponding in-world reactant block.")
                .pointAt(util.vector().centerOf(ventPos));

        scene.idle(105);

        scene.world().showSection(pipe.add(tank).add(pipe).add(util.select().position(pumpPos)), Direction.SOUTH);


        scene.overlay().showText(50)
                .attachKeyFrame()
                .placeNearTarget()
                .text("This is a Lava source block by default.")
                .pointAt(util.vector().centerOf(pumpPos));
        scene.idle(20);
        scene.world().showSection(kenetics, Direction.EAST);
        scene.idle(15);
        scene.world().setKineticSpeed(util.select().position(pumpPos), 64);
        scene.world().setKineticSpeed(kenetics, -64);
        scene.world().showSection(util.select().position(pumpPos.relative(Direction.NORTH, 1)), Direction.DOWN);
        scene.idle(30);
        scene.world().setBlock(pumpPos.relative(Direction.NORTH, 1), Blocks.LAVA.defaultBlockState(), true);
        scene.idle(1);
        scene.world().setBlock(pumpPos.relative(Direction.NORTH, 1), AllPaletteStoneTypes.CRIMSITE.baseBlock.get().defaultBlockState(), true);

//        scene.effects().emitParticles(util.vector().blockSurface(new BlockPos(2, 1, 4), Direction.NORTH), (level, x, y, z) -> {
//
//        } );

//        scene.addKeyframe();

        scene.idle(30);





//        scene.world().showSection(util.select().layersFrom(0), Direction.DOWN);



    }
}
