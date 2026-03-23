package me.imgalvin.playerfinder;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerFinder implements ModInitializer {
	PlayerFinderUtils utils = new PlayerFinderUtils();

    public static final Logger LOGGER = LoggerFactory.getLogger("PlayerFinder");

	@Override
	public void onInitialize() {
        LOGGER.info("PlayerFinder initialized!");
		// _ previously registryAccess, environment
		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> dispatcher.register(Commands.literal("findplayer")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            LOGGER.info("Executing /findplayer command");

                            ServerPlayer targetPlayerName = EntityArgument.getPlayer(context, "player");
                            String playerName = targetPlayerName.getGameProfile().name();

                            ServerPlayer targetPlayer = context.getSource().getServer().getPlayerList().getPlayer(playerName);
                            ServerPlayer sourcePlayer = context.getSource().getServer().getPlayerList().getPlayer(context.getSource().getTextName());

                            if (targetPlayer == null) {
                                context.getSource().sendSystemMessage(Component.literal("[PlayerFinder ERROR] Player " + playerName + " not found").withStyle(ChatFormatting.RED));
                                return 0;
                            }
                            if (sourcePlayer == null) {
                                context.getSource().sendSystemMessage(Component.literal("[PlayerFinder ERROR] Could not determine command source player").withStyle(ChatFormatting.RED));
                                return 0;
                            }

                            BlockPos targetBlockPos = targetPlayer.blockPosition();
                            BlockPos sourceBlockPos = sourcePlayer.blockPosition();

                            LOGGER.info("Target player position: {}", targetBlockPos);
                            LOGGER.info("Source player position: {}", sourceBlockPos);

                            ResourceKey<Level> playerDimension = targetPlayer.level().getLevel().dimension();
                            ResourceKey<Level> sourceDimension = sourcePlayer.level().getLevel().dimension();

                            LOGGER.info("Target player dimension: {}", playerDimension);
                            LOGGER.info("Source player dimension: {}", sourceDimension);

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