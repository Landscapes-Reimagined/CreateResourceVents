package com.landscapesreimagined.createresourcevents.Config;

import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BlockStateHolder {

    public static BlockStateHolder.JsonSerialization SERIALIZATION = new BlockStateHolder.JsonSerialization();

    public final Map<String, String> properties = new HashMap<>();
    public final ResourceLocation id;

    public BlockStateHolder(ResourceLocation blockID){
        this.id = blockID;
    }


    public void addProperty(String propertyName, String propertyValue){
        this.properties.put(propertyName, propertyValue);
    }

    public void removeProperty(String propertyName){
        this.properties.remove(propertyName);
    }

    @Nullable
    public BlockState build(){
        var blockHolderOptional = ForgeRegistries.BLOCKS.getHolder(this.id);

        if(blockHolderOptional.isEmpty()){
            return null;
        }

        var blockHolder = blockHolderOptional.get();

        if(blockHolderOptional.isEmpty()){
            return null;
        }

        Block block = blockHolder.get();

        BlockState state = block.defaultBlockState();
        StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();


        for(String key : properties.keySet()){
            Property<?> keyProp = stateDefinition.getProperty(key);

            if(keyProp == null){
                continue;
            }

            BlockState modifiedState = setProperty(key, keyProp, state);

            if(modifiedState == null) continue;

            state = modifiedState;
        }

        return state;
    }

    public static BlockStateHolder create(BlockState fromState){
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(fromState.getBlock());

        BlockStateHolder holder = new BlockStateHolder(id);

        if(fromState.equals(fromState.getBlock().defaultBlockState()))
            return holder;

        for(Property<?> property : fromState.getProperties()){
            holder.addProperty(property.getName(), Objects.toString(fromState.getValue(property)));
        }

        return holder;
    }

    @Nullable
    private <T extends Comparable<T>> BlockState setProperty(String key, Property<T> keyProp, BlockState state) {
        Optional<T> propertyValue = keyProp.getValue(properties.get(key));

        if(propertyValue.isEmpty()) return null;

        state = state.setValue(keyProp, propertyValue.get());
        return state;
    }

    public static class JsonSerialization implements JsonSerializer<BlockStateHolder>,
            JsonDeserializer<BlockStateHolder> {


        @Override
        public JsonElement serialize(BlockStateHolder blockStateHolder,
                                     Type type,
                                     JsonSerializationContext jsonSerializationContext
        ) {

            JsonObject holder = new JsonObject();

            holder.addProperty("id", blockStateHolder.id.toString());

            JsonArray propertyArray = new JsonArray();

            for(String key : blockStateHolder.properties.keySet()){
                propertyArray.add(key + "="  + blockStateHolder.properties.get(key));
            }

            holder.add("properties", propertyArray);

            return holder;
        }

        @Override
        public BlockStateHolder deserialize(JsonElement jsonElement,
                                            Type type,
                                            JsonDeserializationContext jsonDeserializationContext
        ) throws JsonParseException {

            JsonObject holder = jsonElement.getAsJsonObject();

            ResourceLocation id = ResourceLocation.parse(holder.get("id").getAsString());

            BlockStateHolder stateHolder = new BlockStateHolder(id);

            JsonArray propertyArray = holder.getAsJsonArray("properties");

            for(JsonElement propertyElement : propertyArray){
                String property = propertyElement.getAsString();

                stateHolder.addProperty(property.substring(0, property.indexOf('=')),
                        property.substring(property.indexOf('=') + 1));

            }

            return stateHolder;
        }


    }
}
