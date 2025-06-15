package com.landscapesreimagined.createresourcevents.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BiomePredicatePlacementModifier extends PlacementModifier {

    public static final Codec<BiomePredicatePlacementModifier> CODEC = RecordCodecBuilder.create(
            (instance) -> {
                return instance.group(ResourceLocation.CODEC.listOf().optionalFieldOf("biome", Collections.emptyList()).forGetter((vars) ->{
                    return vars.predicate;
                }), ResourceLocation.CODEC.optionalFieldOf("biomeTag", null).forGetter((vars) -> {
                    return vars.tag;
                }), PlacementModifier.CODEC.listOf().optionalFieldOf("true", Collections.emptyList()).forGetter((vars) -> {
                    return vars.valIfPredExists;
                }), PlacementModifier.CODEC.listOf().optionalFieldOf("false", Collections.emptyList()).forGetter((vars) ->{
                    return vars.valIfPredDoesNotExist;
                })
                ).apply(instance, BiomePredicatePlacementModifier::new);
            }
    );

    private final List<ResourceLocation> predicate;
    private final ResourceLocation tag;
    private final List<PlacementModifier> valIfPredExists;
    private final List<PlacementModifier> valIfPredDoesNotExist;

    public BiomePredicatePlacementModifier(List<ResourceLocation> predicate, ResourceLocation tag, List<PlacementModifier> valIfPredExists, List<PlacementModifier> valIfPredDoesNotExist) {
        this.predicate = predicate;
        this.tag = tag;
        this.valIfPredExists = valIfPredExists;
        this.valIfPredDoesNotExist = valIfPredDoesNotExist;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext pContext, RandomSource pRandom, BlockPos pPos) {




        var pms = this.predicate.stream().anyMatch((p) -> pContext.getLevel().getBiome(pPos).is(p))  ? this.valIfPredExists : this.valIfPredDoesNotExist;

        if(this.tag != null){
            pms = pContext.getLevel().getBiome(pPos).is(TagKey.create(Registries.BIOME, this.tag)) ? this.valIfPredExists : this.valIfPredDoesNotExist;
        }

        var poses = pms.stream().map((pm) -> {
            return pm.getPositions(pContext, pRandom, pPos);
        }).map(Stream::toList).toList();
        var list = new ArrayList<BlockPos>();
        for(List<BlockPos> pos : poses){
            list.addAll(pos);
        }

        return list.stream();
    }

    @Override
    public PlacementModifierType<?> type() {
        return null;
    }

    public List<PlacementModifier> getValIfPredDoesNotExist() {
        return valIfPredDoesNotExist;
    }

    public List<PlacementModifier> getValIfPredExists() {
        return valIfPredExists;
    }

    public List<ResourceLocation> getPredicate() {
        return predicate;
    }

    public ResourceLocation getTag() {
        return tag;
    }
}
