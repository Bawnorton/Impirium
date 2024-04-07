package com.bawnorton.impirium;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ImPIriumClient implements ClientModInitializer {
	private boolean showedWarning;

	@Override
	public void onInitializeClient() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			showedWarning = false;
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if(client.isInSingleplayer()) return;

			if(ImPIrium.NEW_PI != 3.141592653589793) {
				client.inGameHud.getChatHud().addMessage(Text.literal("Warning: your PI is modified on a public server, you may get banned by an anticheat.")
						.formatted(Formatting.RED, Formatting.BOLD));
			}

			dispatcher.register(ClientCommandManager.literal("getpiclient")
					.executes(context -> {
						context.getSource().sendFeedback(Text.of("Client pi is " + ImPIrium.NEW_PI));
						return 1;
					})
			);
			dispatcher.register(ClientCommandManager.literal("setpiclient")
					.then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg())
							.executes(context -> {
								double pi = DoubleArgumentType.getDouble(context, "value");

								if(!showedWarning && pi != 3.141592653589793) {
									showedWarning = true;
									context.getSource()
											.sendFeedback(Text.literal("Warning: you may get banned by an anticheat." +
															"\nAre you sure? (run again to confirm)")
													.formatted(Formatting.RED, Formatting.BOLD));
									return 1;
								}

								ImPIrium.NEW_PI = pi;
								ImPIrium.updateValues();
								context.getSource().sendFeedback(Text.of("Client pi set to " + ImPIrium.NEW_PI));
								return 1;
							})
					)
			);
		});
	}
}