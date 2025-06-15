package com.landscapesreimagined.createresourcevents.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

@Mixin(ChunkSerializer.class)
public class ChunkParserMixin {

    @WrapOperation(method = "read", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;", ordinal = 0))
    private static DataResult<BlockState> redirectParse(Codec instance, DynamicOps dynamicOps, Object o, Operation<DataResult> original){

        if(o instanceof CompoundTag tag){

            tag.put("palette", tag.getList("palette", 10).stream().map((tag1) -> {
                if(tag1 instanceof CompoundTag entry){
                    String name = entry.getString("Name");
                    if(name.contains("molten_vents")){
                        String stN = name.replace("molten_vents:", "");
                        String stnm = stN.replace("molten_", "");
                        String val = "create_resource_vents:" + stnm + "_vent";
                        entry.putString("Name", val);
                    }
                }
                return tag1;
            }).collect(new Collector<Tag, ListTag, Tag>() {
                @Override
                public Supplier<ListTag> supplier() {
                    return ListTag::new;
                }

                @Override
                public BiConsumer<ListTag, Tag> accumulator() {
                    return ListTag::add;
                }

                @Override
                public BinaryOperator<ListTag> combiner() {
                    return (t1, t2) -> {
                        t1.addAll(t2);
                        return t1;
                    };
                }

                @Override
                public Function<ListTag, Tag> finisher() {
                    return (tags -> tags);
                }

                @Override
                public Set<Characteristics> characteristics() {
                    return Set.of();
                }
            }));

        }

        return original.call(instance, dynamicOps, o);
    }



}
