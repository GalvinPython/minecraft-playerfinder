package me.imgalvin.playerfinder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PlayerFinderUtils {
    public ChatFormatting getDimensionColor(@NotNull ResourceKey<Level> playerDimension) {
        return playerDimension.equals(ServerLevel.OVERWORLD) ? ChatFormatting.GREEN :
                playerDimension.equals(ServerLevel.NETHER) ? ChatFormatting.RED :
                        playerDimension.equals(ServerLevel.END) ? ChatFormatting.LIGHT_PURPLE :
                                ChatFormatting.GRAY; // Fallback colour for custom or unknown dimensions
    }

    public String getDimensionText(@NotNull ResourceKey<Level> playerDimension) {
        return playerDimension.identifier().getPath().replace("the_", "");
    }

    public int getDistance(@NotNull BlockPos playerPos, @NotNull BlockPos targetPos) {
        System.out.println("Calculating distance between " + playerPos + " and " + targetPos);
        return (int) Math.sqrt(Math.pow(playerPos.getX() - targetPos.getX(), 2) + Math.pow(playerPos.getY() - targetPos.getY(), 2) + Math.pow(playerPos.getZ() - targetPos.getZ(), 2));
    }
}
