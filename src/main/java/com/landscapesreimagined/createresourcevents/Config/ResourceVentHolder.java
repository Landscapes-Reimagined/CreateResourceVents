package com.landscapesreimagined.createresourcevents.Config;

import com.google.gson.*;
import com.landscapesreimagined.createresourcevents.Blocks.ActiveResourceVentBlock;
import com.landscapesreimagined.createresourcevents.CreateResourceVents;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResourceVentHolder {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceVentHolder.JsonSerialization SERIALIZATION = new ResourceVentHolder.JsonSerialization();


    public List<BlockStateHolder> generationStates;

    /** will be null iff usesFluid */
    @UnknownNullability
    private List<BlockStateHolder> catalystBlockStates;

    /** will be null iff not usesFluid */
    @UnknownNullability
    private List<ResourceLocation> catalystFluids;

    private boolean usesFluid;
    public int maxGenerationDistance = 2;

    /**
     * To be formatted with dormant_{ventName}_vent and active_{ventName}_vent
     */
    public final String ventName;


    public ResourceVentHolder(String name){

        this.ventName = name;

    }

    public ResourceVentHolder setMaxGenerationDistance(int generationDistance){
        this.maxGenerationDistance = generationDistance;
        return this;
    }

    public ResourceVentHolder setGenerationStates(List<BlockStateHolder> generationStates) {
        this.generationStates = generationStates;
        return this;
    }

    public ResourceVentHolder addGenerationState(BlockStateHolder... generationState){

        if(this.generationStates == null){
            this.generationStates = new ArrayList<BlockStateHolder>();
        }

        this.generationStates.addAll(Arrays.asList(generationState));

        return this;
    }

    public @UnknownNullability List<BlockStateHolder> getCatalystBlockStates() {
        return catalystBlockStates;
    }

    public @UnknownNullability List<ResourceLocation> getCatalystFluids() {
        return catalystFluids;
    }

    public boolean usesFluid() {

        if(this.catalystFluids == null){
            this.usesFluid = false;
        }else{
            this.usesFluid = true;
        }

        return this.usesFluid;
    }

    public ResourceVentHolder setCatalystBlockStates(@NonNull List<BlockStateHolder> catalystBlockStates) {
        if(ensureUsesFluid()) return this;
        
        this.catalystBlockStates = catalystBlockStates;
        return this;
    }


    public ResourceVentHolder setCatalystFluids(@NonNull List<ResourceLocation> catalystFluids) {
        if(usesBlockStates()) return this;

        this.catalystFluids = catalystFluids;
        return this;
    }

    public ResourceVentHolder addCatalystBlockState(BlockStateHolder... catalystBlockStates){

        if(ensureUsesFluid()) return this;

        if(this.catalystBlockStates == null){
            this.catalystBlockStates = new ArrayList<>();
        }

        this.catalystBlockStates.addAll(Arrays.asList(catalystBlockStates));

        return this;
    }

    public ResourceVentHolder addCatalystFluid(ResourceLocation... catalystFluids){

        if (usesBlockStates()) return this;

        if(this.catalystFluids == null){
            this.catalystFluids = new ArrayList<>();
        }

        this.catalystFluids.addAll(Arrays.asList(catalystFluids));

        return this;

    }

    private boolean usesBlockStates() {
        if(!usesFluid && this.catalystBlockStates != null){
            this.usesFluid = false;
            LOGGER.error("Tried to set catalystFluids on a BlockState-based vent!");
            return true;
        }
        this.usesFluid = true;
        return false;
    }

    private boolean ensureUsesFluid() {
        if(usesFluid && this.catalystFluids != null){
            this.usesFluid = true;
            LOGGER.error("Tried to set catalystBlockStates on a Fluid-based vent!");
            return true;
        }
        this.usesFluid = false;
        return false;
    }

    public ActiveResourceVentBlock build(BlockBehaviour.Properties properties){

        List<BlockState> generationStates = buildGenerationStates();

        List<BlockState> reactantBlocks = null;
        List<Fluid> reactantFluids = null;

        if(this.usesFluid()){
            reactantFluids = buildReactantFluids();
        }else{
            reactantBlocks = buildReatantBlocks();
        }

        return new ActiveResourceVentBlock(properties,
                    generationStates,
                    reactantBlocks,
                    reactantFluids,
                    maxGenerationDistance,
                ventName);
    }

    @NotNull
    public List<BlockState> buildGenerationStates() {
        List<BlockState> generationStates = this.generationStates.stream()
                .map(BlockStateHolder::build)
                .collect(Collectors.toList());
        return generationStates;
    }

    @NotNull
    public List<Fluid> buildReactantFluids() {
        List<Fluid> reactantFluids;
        reactantFluids = this.catalystFluids.stream()
                .map(ForgeRegistries.FLUIDS::getValue)
                .filter(Predicate.not(Objects::isNull))
                .collect(Collectors.toList());
        return reactantFluids;
    }

    @NotNull
    public List<BlockState> buildReatantBlocks() {
        List<BlockState> reactantBlocks;
        reactantBlocks = this.catalystBlockStates.stream()
                .map(BlockStateHolder::build)
                .collect(Collectors.toList());
        return reactantBlocks;
    }


    public static class JsonSerialization implements JsonSerializer<ResourceVentHolder>,
            JsonDeserializer<ResourceVentHolder>{

        @Override
        public ResourceVentHolder deserialize(JsonElement json,
                                              Type typeOfT,
                                              JsonDeserializationContext context
        ) throws JsonParseException {

            JsonObject holder = json.getAsJsonObject();

            String name = holder.get("name").getAsString();

            ResourceVentHolder ventHolder = new ResourceVentHolder(name);

            if(holder.has("maxGenerationDistance")) {
                ventHolder.setMaxGenerationDistance(holder.get("maxGenerationDistance").getAsInt());
            }

            JsonArray generationStates = holder.getAsJsonArray("generatedBlocks");

            for(JsonElement e : generationStates){
                BlockStateHolder generatedStateHolder = context.deserialize(e, BlockStateHolder.class);
                ventHolder.addGenerationState(generatedStateHolder);
            }

            boolean ventUsesFluid = holder.has("reactantFluids");

            ventHolder.usesFluid = ventUsesFluid;

            JsonArray generationArray = ventUsesFluid
                    ? holder.getAsJsonArray("reactantFluids")
                    : holder.getAsJsonArray("reactantBlocks");

            for(JsonElement e : generationArray){
                if(ventUsesFluid){
                    deserializeFluids(e, ventHolder, context);
                }else{
                    deserializeCatalystStates(e, ventHolder, context);
                }
            }



            return ventHolder;
        }


        public void deserializeFluids(JsonElement e, ResourceVentHolder ventHolder, JsonDeserializationContext ctx) throws JsonParseException{
            String fluidLocationString = e.getAsString();

            if(fluidLocationString.isEmpty()){
                return;
            }

            ResourceLocation fluidLocation = ResourceLocation.parse(fluidLocationString);


            ventHolder.addCatalystFluid(fluidLocation);

        }

        public void deserializeCatalystStates(JsonElement e, ResourceVentHolder ventHolder, JsonDeserializationContext ctx) throws JsonParseException{

            BlockStateHolder blockStateHolder = ctx.deserialize(e, BlockStateHolder.class);


            ventHolder.addCatalystBlockState(blockStateHolder);

        }

        @Override
        public JsonElement serialize(ResourceVentHolder src,
                                     Type typeOfSrc,
                                     JsonSerializationContext context
        ) {
            JsonObject holder = new JsonObject();

            holder.addProperty("name", src.ventName);

            if(src.maxGenerationDistance != 2){
                holder.addProperty("maxGenerationDistance", src.maxGenerationDistance);
            }

            JsonArray generatedStates = new JsonArray();

            for(BlockStateHolder state : src.generationStates){

                generatedStates.add(context.serialize(state, BlockStateHolder.class));
            }

            holder.add("generatedBlocks", generatedStates);

            if(src.usesFluid()){
                holder.add("reactantFluids", serializeReactantFluids(src, context));
            }else{
                holder.add("reactantBlocks", serializeReactantBlocks(src, context));
            }


            return holder;
        }

        public JsonArray serializeReactantFluids(ResourceVentHolder src, JsonSerializationContext ctx) {
            JsonArray fluidArray = new JsonArray();

            for(ResourceLocation loc : src.catalystFluids){
                fluidArray.add(loc.toString());
            }

            return fluidArray;
        }

        public JsonArray serializeReactantBlocks(ResourceVentHolder src, JsonSerializationContext ctx){
            JsonArray blockArray = new JsonArray();

            for(BlockStateHolder state : src.catalystBlockStates){
                blockArray.add(ctx.serialize(state, BlockStateHolder.class));
            }

            return blockArray;
        }
    }

}
