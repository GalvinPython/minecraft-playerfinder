package me.imgalvin.playerfinder;

import net.minecraft.ChatFormatting;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class PlayerFinderUtils {
    public ChatFormatting getDimensionColor(@NotNull RegistryKey<World> playerDimension) {
        return playerDimension.equals(World.OVERWORLD) ? ChatFormatting.GREEN :
                playerDimension.equals(World.NETHER) ? ChatFormatting.RED :
                        playerDimension.equals(World.END) ? ChatFormatting.LIGHT_PURPLE :
                                ChatFormatting.GRAY; // Fallback colour for custom or unknown dimensions
    }

    public String getDimensionText(@NotNull RegistryKey<World> playerDimension) {
        // note: this function only works for vanilla dimensions. custom dimensions will have a slight issue
        return playerDimension.getValue().toString().split(":")[1].replace("the_", "");
    }

    public int getDistance(@NotNull BlockPos playerPos, @NotNull BlockPos targetPos) {
        return (int) Math.sqrt(Math.pow(playerPos.getX() - targetPos.getX(), 2) + Math.pow(playerPos.getY() - targetPos.getY(), 2) + Math.pow(playerPos.getZ() - targetPos.getZ(), 2));
    }
}
