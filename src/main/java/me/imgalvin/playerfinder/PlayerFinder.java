package me.imgalvin.playerfinder;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class PlayerFinder implements ModInitializer {
	PlayerFinderUtils utils = new PlayerFinderUtils();

	@Override
	public void onInitialize() {
		// _ previously registryAccess, environment
		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> dispatcher.register(Commands.literal("findplayer")
                .then(Commands.argument("player", EntityArgument.entity())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            ServerPlayer targetPlayer = context.getSource().getServer().getPlayerList().getPlayer(playerName);
                            ServerPlayer sourcePlayer = context.getSource().getPlayer();

                            assert targetPlayer != null;
                            assert sourcePlayer != null;

                            BlockPos targetBlockPos = targetPlayer.blockPosition();
                            BlockPos sourceBlockPos = sourcePlayer.blockPosition();
                            ResourceKey<Level> playerDimension = targetPlayer.level().getLevel().dimension();
                            ResourceKey<Level> sourceDimension = sourcePlayer.level().getLevel().dimension();

                            boolean isSameDimension = sourceDimension == playerDimension;

                            Component message = Component.literal(playerName + " is at ")
                                    .append(Component.literal(targetBlockPos.getX() + ", " + targetBlockPos.getY() + ", " + targetBlockPos.getZ())
                                            .withStyle(utils.getDimensionColor(playerDimension)))
                                    .append(Component.literal(" in the ").withStyle(ChatFormatting.WHITE))
                                    .append(Component.literal(utils.getDimensionText(playerDimension))
                                            .withStyle(utils.getDimensionColor(playerDimension)))
                                    .append(Component.literal(isSameDimension
                                                    ? " (" + utils.getDistance(sourceBlockPos, targetBlockPos) + " blocks away)"
                                                    : " (Player is in a different dimension)")
                                            .withStyle(isSameDimension ? ChatFormatting.GREEN : ChatFormatting.RED));

                            // Send it as a system message to the source
                            context.getSource().sendSystemMessage(message);

                            return 1;
                        })
                )
        ));
	}
}