package com.landscapesreimagined.createresourcevents.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import oshi.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ResourceVentFeature extends Feature<ResourceVentFeatureConfiguration> {


    public ResourceVentFeature(Codec<ResourceVentFeatureConfiguration> p_65786_) {
        super(p_65786_);
    }

    public static final double sqrt2 = Math.sqrt(2.0d);


    @Override
    protected void setBlock(LevelWriter pLevel, BlockPos pPos, BlockState pState) {
        super.setBlock(pLevel, pPos, pState);
        if(pLevel instanceof WorldGenLevel wgl){

            if(wgl.getBiome(pPos).is(BiomeTags.IS_OCEAN) && pPos.getY() < 63 && pState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                super.setBlock(pLevel, pPos, pState.setValue(BlockStateProperties.WATERLOGGED, true));

            }
            if(wgl.getBiome(pPos).is(BiomeTags.IS_OCEAN) && pPos.getY() < 63 && pState.isAir()){
                super.setBlock(pLevel, pPos, Blocks.WATER.defaultBlockState());
            }

            wgl.blockUpdated(pPos, pState.getBlock());
        }
    }

    @Override
    @NotNull
    public boolean place(FeaturePlaceContext<ResourceVentFeatureConfiguration> ctx) {

        //constants

        BlockPos origin = ctx.origin();
        WorldGenLevel level = ctx.level();
        RandomSource rand = ctx.random();

        ResourceVentFeatureConfiguration config = ctx.config();

        //above ground parameters
        int diameter = config.getPortionSize();
        double trueRadius = diameter / 2.0d;
        int iterationBoundRadius = Mth.ceil(trueRadius);
        int maxWallHeight = config.getRiseAmount();
        int wallDiameter = diameter + 2;
        double trueWallRadius = wallDiameter / 2.0d;
        int iterationBoundWallRadius = Mth.ceil(trueWallRadius);
        int yWallBound = maxWallHeight + 1;
        final int wallHeightOffset = 1;


        IntProvider maxCoulmnHeight = config.getStiffPeakSize();
        int columnHeight = maxCoulmnHeight.sample(rand);
//        int columnHeightOffset = Mth.abs(columnHeight - yWallBound);
        BlockStateProvider baseStones = config.getSolidIngredients();
        List<BlockStateProvider> decorations = config.getSprinkles();
        BlockStateProvider dormantVentType = config.getFlavoring();

        boolean clearArea = config.getMakeSpaceInOven();
//
//        //below ground parameters
        BlockStateProvider innerFluid = config.getGooeyLiquidCenter();
        IntProvider maxDepth = config.getDepth();
        int depth = maxDepth.sample(rand);



        int minX = Math.min(-iterationBoundRadius, -iterationBoundWallRadius);
        int maxX = Math.max(iterationBoundRadius, iterationBoundWallRadius);

        int minY = Math.min(0, yWallBound + columnHeight);
        int maxY = Math.max(0, yWallBound + columnHeight);

        int minZ = Math.min(-iterationBoundRadius, -iterationBoundWallRadius);
        int maxZ = Math.max(iterationBoundRadius, iterationBoundWallRadius);

        int[] maxDirection = new int[6];

        for(int dx = minX - 1; dx < maxX + 1; dx++){
            for(int dy = 1; dy < maxY * 2; dy++) {
                for (int dz = minZ - 1; dz < maxZ + 1; dz++) {

                    BlockPos offset = new BlockPos(dx, dy, dz);
                    BlockPos originOffset = origin.offset(offset);
                    BlockPos offsetWithoutY = new BlockPos(dx, 0, dz);


                    if(dy < maxY){
                        if(distanceByOffset(offsetWithoutY) <= (wallDiameter) / 2.0d){
                            this.setBlock(level, originOffset, Blocks.AIR.defaultBlockState());

                        }
                    }else {

                        if (distanceFromOrigin(originOffset, origin.above(maxY - 1)) <= (wallDiameter) / 2.0d) {
                            this.setBlock(level, originOffset, Blocks.AIR.defaultBlockState());
                        }
                    }

                }
            }
        }





        for(int dx = minX; dx < maxX; dx++){
            for(int dy = minY; dy < maxY; dy++) {
                int destroyChance = Mth.ceil(Math.max(0.0, 1/3.0d * (-1 * (dy - 2) * (dy - 2) + 1)) * 17.5 );
                for (int dz = minZ; dz < maxZ; dz++) {

                    maxDirection = placeOuterShell(
                            dx, dy, dz,
                            origin, trueRadius,
                            level, baseStones,
                            rand, maxDirection,
                            trueWallRadius, maxWallHeight,
                            columnHeight, destroyChance
                    );

                }
            }
        }



        //decorate
        placeDecorations(
                trueWallRadius,
                minX, maxX,
                maxY,
                minZ, maxZ,
                origin, level,
                decorations, rand
        );

        for(int dx = minX; dx < maxX; dx++){
            for(int dy = 0; dy >= -depth; dy--){
                for(int dz = minZ; dz < maxZ; dz++){
                    BlockPos offsetWithoutY = new BlockPos(dx, 0, dz);
                    BlockPos offset = new BlockPos(dx, dy, dz);
                    BlockPos originOffset = origin.offset(offset);





                    double distanceFromFluid = distanceByOffset(offsetWithoutY);
                    if(dy > -depth/2 && distanceFromFluid <= 2.5) {

                        double keepChance = Math.abs((depth * ((double) (2 * dy) / -depth) - depth) / (depth));

                        double sample = rand.nextDouble();

                        if (sample <= keepChance) {
                            this.setBlock(level, originOffset, baseStones.getState(rand, originOffset));
                        }
                    }

                    if(distanceFromFluid > 1.5 ) continue;

                    double keepChance = Math.abs((depth * ((double) dy / -depth) - depth) / (depth));
                    double sample = rand.nextDouble();

                    if (sample > keepChance) continue;

                    this.setBlock(level, originOffset, baseStones.getState(rand, originOffset));





                }
            }
        }


        for(int dy = 1; dy < depth; dy++){
            this.setBlock(level, origin.below(dy), innerFluid.getState(rand, origin.above(dy)));
        }

        this.setBlock(level, origin, dormantVentType.getState(rand, origin));
        for(int dy = 1; dy < maxY; dy++){
            this.setBlock(level, origin.above(dy), Blocks.AIR.defaultBlockState());
            level.blockUpdated(origin.above(dy), level.getBlockState(origin.above(dy)).getBlock());
        }
        level.blockUpdated(origin.above(maxY + 1), level.getBlockState(origin.above(maxY + 1)).getBlock());

        ;

        return true;
    }

    private void placeDecorations(double trueWallRadius, int minX, int maxX, int maxY, int minZ, int maxZ, BlockPos origin, WorldGenLevel level, List<BlockStateProvider> decorations, RandomSource rand) {
        List<BlockPos> decorationPlacements = collectDecorationPlacements(
                trueWallRadius,
                minX, maxX,
                maxY,
                minZ, maxZ,
                origin, level);

        Collections.shuffle(decorationPlacements);

        List<BlockStateProvider> nonImmutableList = new ArrayList<>(decorations);

        int innerCircleCredits = 2;

        for(BlockPos p : decorationPlacements){
            double distFromOrigin = distanceFromOrigin(new BlockPos(p.getX(), origin.getY(), p.getZ()), origin);

            if(distFromOrigin <= sqrt2 + 0.2){

                if(0 >= innerCircleCredits){
                    continue;
                }

                innerCircleCredits -= 1;
            }

            if(rand.nextIntBetweenInclusive(1, 10) >= 6){

                Collections.shuffle(nonImmutableList);
                BlockStateProvider provider = nonImmutableList.get(0);
                int discardSDP = rand.nextIntBetweenInclusive(1, 4);
                if(provider instanceof StateDataProvider ){
                    if(discardSDP >= 3) continue;
                    if(discardSDP == 2 && nonImmutableList.size() >= 2){
                        provider = nonImmutableList.get(1);

                    }
                }
                BlockState s = provider.getState(rand, p);

                for(Direction d : Direction.Plane.HORIZONTAL.stream().toList()){
                    s = s.updateShape(d, level.getBlockState(p.relative(d)), level, p, p.relative(d));
                }



                this.setBlock(level, p, s);



                if(provider instanceof StateDataProvider sdp){
                    CompoundTag tag = sdp.getNbt(rand, p);

                    var be = level.getBlockEntity(p);
                    if(be != null){
                        be.load(be.saveWithFullMetadata().merge(tag));
                    }

                }

                if(level.getBlockState(p).hasProperty(HorizontalDirectionalBlock.FACING)){
                    Direction d = getFacingDirectionForPos(p, level, origin);
                    if(d != null)
                        level.setBlock(p, level.getBlockState(p).setValue(HorizontalDirectionalBlock.FACING, d), 3);
                }

            }
        }
    }

    private int[] placeOuterShell(int dx, int dy, int dz, BlockPos origin, double trueRadius, WorldGenLevel level, BlockStateProvider baseStones, RandomSource rand, int[] maxDirection, double trueWallRadius, int maxWallHeight, int columnHeight, int destroyChance) {
        BlockPos offset = new BlockPos(dx, dy, dz);
        BlockPos originOffset = origin.offset(offset);
        BlockPos offsetWithoutY = new BlockPos(dx, 0, dz);


        if (dy == 0 && distanceByOffset(offset) <= trueRadius) {
            this.setBlock(level, originOffset, baseStones.getState(rand, originOffset));
            if(baseStones instanceof StateDataProvider sdp){
                CompoundTag tag = sdp.getNbt(rand, originOffset);

                var be = level.getBlockEntity(originOffset);
                if(be != null){
                    be.load(be.saveWithFullMetadata().merge(tag));
                }


            }


            int placeBlockAbove = rand.nextIntBetweenInclusive(1, 10);
            if(placeBlockAbove == 6 && distanceByOffset(offsetWithoutY) > sqrt2 + 0.2){
                this.setBlock(level, originOffset.above(), baseStones.getState(rand, originOffset.above()));
                if(baseStones instanceof StateDataProvider sdp){
                    CompoundTag tag = sdp.getNbt(rand, originOffset.above());

                    var be = level.getBlockEntity(originOffset.above());
                    if(be != null){
                        be.load(be.saveWithFullMetadata().merge(tag));
                    }


                }
            }
            maxDirection = updateMaxDir(maxDirection, originOffset);
        }else if(dy > 0 && distanceByOffset(offset) <= trueRadius && (rand.nextDouble() <= 0.6 && !level.getBlockState(originOffset.below()).isAir())){
            this.setBlock(level, originOffset, Blocks.AIR.defaultBlockState());
        }

        if(dy > 0 && wallPlaceCondition(offset, trueWallRadius) && dy < (maxWallHeight + columnHeight)){

            int num = (rand.nextIntBetweenInclusive(1, 10));

            if(num > destroyChance){
                this.setBlock(level, originOffset, baseStones.getState(rand, originOffset));
                if(baseStones instanceof StateDataProvider sdp){
                    CompoundTag tag = sdp.getNbt(rand, originOffset);

                    var be = level.getBlockEntity(originOffset);
                    if(be != null){
                        be.load(be.saveWithFullMetadata().merge(tag));
                    }


                }
                maxDirection = updateMaxDir(maxDirection, originOffset);


            }




        }

        if(dy >= (maxWallHeight + columnHeight) && wallPlaceCondition(offset, trueWallRadius) && offsetWithoutY.distManhattan(BlockPos.ZERO) > trueWallRadius){

            if(!isNotActualCorner(offsetWithoutY, trueWallRadius)) {

                if(rand.nextIntBetweenInclusive(1, 5) <= 4) {
                    this.setBlock(level, originOffset, baseStones.getState(rand, originOffset));
                }
                this.setBlock(level, originOffset.below(), baseStones.getState(rand, originOffset));

                if(baseStones instanceof StateDataProvider sdp){
                    CompoundTag tag = sdp.getNbt(rand, originOffset);
                    CompoundTag btag = sdp.getNbt(rand, originOffset.below());

                    var be = level.getBlockEntity(originOffset);
                    var bbe = level.getBlockEntity(originOffset.below());
                    if(be != null){
                        be.load(be.saveWithFullMetadata().merge(tag));
                    }

                    if(bbe != null){
                        bbe.load(bbe.saveWithFullMetadata().merge(btag));
                    }

                }
            }


        }
        return maxDirection;
    }

    private Direction getFacingDirectionForPos(BlockPos p, WorldGenLevel level, BlockPos origin) {

        Vec3 centerToP = origin.getCenter().subtract(new BlockPos(p.getX(), origin.getY(), p.getZ()).getCenter());

        Direction returnCandidate =  Direction.getNearest(centerToP.x, centerToP.y, centerToP.z).getOpposite();

        if (!level.getBlockState(p.relative(returnCandidate)).isAir() && level.getBlockState(p.relative(returnCandidate)).isFaceSturdy(level, p, returnCandidate.getOpposite())) {
            return returnCandidate;
        }

        for (Direction d : Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(p.relative(d)).isAir() && level.getBlockState(p.relative(d)).isFaceSturdy(level, p, d.getOpposite())) {
                return d;
            }
        }



        return returnCandidate;
    }

    private static boolean isNotActualCorner(BlockPos offsetWithoutY, double trueWallRadius) {
        boolean cont = false;
        for (Iterator<Direction> it = Direction.Plane.HORIZONTAL.iterator(); it.hasNext(); ) {
            Direction d = it.next();

            BlockPos dOffset = offsetWithoutY.relative(d);

            if(dOffset.distManhattan(BlockPos.ZERO) <= trueWallRadius && wallPlaceCondition(dOffset, trueWallRadius)){
                cont = true;
            }


        }
        return cont;
    }

    private static List<BlockPos> collectDecorationPlacements(
            double trueWallRadius,
            int minX, int maxX,
            int maxY,
            int minZ, int maxZ,
            BlockPos origin,
            WorldGenLevel level
    ){

        ArrayList<BlockPos> decorationPlacements = new ArrayList<>();

        for(int dx = minX; dx < maxX; dx++) {
//            for (int dy = minY; dy < maxY; dy++) {
            uhh:
            for (int dz = minZ; dz < maxZ; dz++) {

                BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos(dx, maxY, dz);
                BlockPos originOffset = origin.offset(offset);


                BlockPos offsetWithoutY = new BlockPos(dx, 0, dz);

                if(distanceByOffset(offsetWithoutY) > trueWallRadius){
                    continue;
                }

                while(!level.getBlockState(origin.offset(offset)).blocksMotion()){
                    if(origin.offset(offset).getY() < origin.getY()){
                        continue uhh;
                    }
                    offset.move(Direction.DOWN);
                }
                offset.move(Direction.UP);

                decorationPlacements.add(origin.offset(offset));


            }
//            }
        }

        return decorationPlacements;

    }

    private static int[] updateMaxDir(int[] maxDirection, BlockPos originOffset) {
        for(Direction d : Direction.values()){

//            if(d == Direction.UP) continue;

            int maxDir = maxDirection[d.ordinal()];
            Direction.Axis axis = d.getAxis();
            int step = d.getAxisDirection().getStep();

            if(step == 1 && maxDir < originOffset.get(axis)){
                maxDir = originOffset.get(axis);
            }else if(step == -1 && maxDir > originOffset.get(axis)){
                maxDir = originOffset.get(axis);
            }

            maxDirection[d.ordinal()] = maxDir;

        }

        return maxDirection;
    }

    private static double distanceFromOrigin(BlockPos p1, BlockPos origin){
        return Math.sqrt(origin.distSqr(p1));
    }

    private static double distanceByOffset(BlockPos delta){
        return Math.sqrt(delta.distSqr(new Vec3i(0, 0, 0)));
    }

    private static boolean wallPlaceCondition(BlockPos origDelta, double wallRadius){
        BlockPos delta = origDelta.below(origDelta.getY());
        return distanceByOffset(delta) <= wallRadius && !(
                    distanceByOffset(delta.south()) <= wallRadius &&
                    distanceByOffset(delta.north()) <= wallRadius &&
                    distanceByOffset(delta.east()) <= wallRadius &&
                    distanceByOffset(delta.west()) <= wallRadius
                );
    }
}
