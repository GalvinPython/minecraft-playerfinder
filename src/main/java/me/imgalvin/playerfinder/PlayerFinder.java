package me.imgalvin.playerfinder;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerFinder implements ModInitializer {
	PlayerFinderUtils utils = new PlayerFinderUtils();

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("findplayer")
					.then(CommandManager.argument("player", StringArgumentType.string())
							.suggests(new PlayerSuggestionProvider())
							.executes(context -> {
								String playerName = StringArgumentType.getString(context, "player");
								PlayerEntity targetPlayer = context.getSource().getServer().getPlayerManager().getPlayer(playerName);
								PlayerEntity sourcePlayer = context.getSource().getPlayer();

								assert targetPlayer != null;
								assert sourcePlayer != null;

								BlockPos targetBlockPos = targetPlayer.getBlockPos();
								BlockPos sourceBlockPos = sourcePlayer.getBlockPos();
								RegistryKey<World> playerDimension = targetPlayer.getEntityWorld().getRegistryKey();
								RegistryKey<World> sourceDimension = sourcePlayer.getEntityWorld().getRegistryKey();

								boolean isSameDimension = sourceDimension == playerDimension;

								context.getSource().sendFeedback(() -> (Text) Text.literal(playerName + " is at ")
                                        .append(Text.literal(targetBlockPos.getX() + ", " + targetBlockPos.getY() + ", " + targetBlockPos.getZ())
                                                .formatted(utils.getDimensionColor(playerDimension)))
                                        .append(Text.literal(" in the ")
												.formatted(Formatting.WHITE))
                                        .append(Text.literal(utils.getDimensionText(playerDimension))
                                                .formatted(utils.getDimensionColor(playerDimension)))
                                        .append(Text.literal(isSameDimension
                                                        ? " (" + utils.getDistance(sourceBlockPos, targetBlockPos) + " blocks away)"
                                                        : " (Player is in a different dimension)")
                                                .formatted(isSameDimension ? Formatting.GREEN : Formatting.RED)), false);
								return 1;
							})
					)
			);
		});
	}
}