package com.bawnorton.impirium;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

public class ImPIriumClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("getpi")
					.executes(context -> {
						context.getSource().sendFeedback(Text.of("Pi is " + ImPIrium.NEW_PI));
						return 1;
					})
			);
			dispatcher.register(ClientCommandManager.literal("setpi")
					.then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg())
							.executes(context -> {
								ImPIrium.NEW_PI = DoubleArgumentType.getDouble(context, "value");
								ImPIrium.updateValues();
								context.getSource().sendFeedback(Text.of("Pi set to " + ImPIrium.NEW_PI));
								return 1;
							})
					)
			);
		});
	}
}