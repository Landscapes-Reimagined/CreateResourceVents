package com.landscapesreimagined.createresourcevents.worldgen;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class StateDataProvider extends BlockStateProvider {
    public static final Codec<StateDataProvider> CODEC = RecordCodecBuilder.create(
            (instance) -> {
                return instance.group(BlockState.CODEC.fieldOf("state").forGetter(
                        (provider) -> {
                            return  provider.state;
                        }), Codec.STRING.fieldOf("nbt").forGetter((provider) -> {
                            return  provider.nbt.toString();
                        })
                ).apply(instance, StateDataProvider::new);

            }
    );

    private final BlockState state;

    private final CompoundTag nbt;

    private static DataResult<StateDataProvider> create(BlockState p_161598_, CompoundTag nbt) {
        return p_161598_ == null ? DataResult.error(() -> {
            return "StateDataProvider with no state or no NBT";
        }) : DataResult.success(new StateDataProvider(p_161598_, nbt));
    }

    public StateDataProvider(BlockState state, CompoundTag nbt) {
        this.state = state;
        this.nbt = nbt;
    }

    public StateDataProvider(BlockState state, String nbt) {
        this.state = state;
        try {
//            System.out.println("got here!");
            this.nbt = TagParser.parseTag(nbt);
        } catch (CommandSyntaxException e) {

            throw new RuntimeException(e);
        }
    }

    @NotNull
    protected BlockStateProviderType<StateDataProvider> type() {
        return ResourceVentsFeatures.STATE_DATA_PROVIDER_TYPE.get();
    }

    @NotNull
    @ParametersAreNonnullByDefault
    public BlockState getState(RandomSource random, BlockPos pos) {
        return this.state;
    }

    @NotNull
    @ParametersAreNonnullByDefault
    public CompoundTag getNbt(RandomSource random, BlockPos pos) {
        return this.nbt;
    }
}
