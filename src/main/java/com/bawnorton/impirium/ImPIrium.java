package com.bawnorton.impirium;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class ImPIrium implements ModInitializer {
	public static double NEW_PI = 3.141592653589793;
	public static double HALF_NEW_PI = NEW_PI / 2;
	public static double QUARTER_NEW_PI = NEW_PI / 4;
	public static double NEW_TAU = NEW_PI * 2;
	public static double NEG_NEW_PI = -NEW_PI;
	public static double NEG_HALF_NEW_PI = -HALF_NEW_PI;
	public static double NEG_QUARTER_NEW_PI = -QUARTER_NEW_PI;
	public static double NEG_NEW_TAU = -NEW_TAU;
	public static float NEW_PI_FLOAT = (float) NEW_PI;
	public static float HALF_NEW_PI_FLOAT = NEW_PI_FLOAT / 2;
	public static float QUARTER_NEW_PI_FLOAT = NEW_PI_FLOAT / 4;
	public static float NEW_TAU_FLOAT = NEW_PI_FLOAT * 2;
	public static float NEG_NEW_PI_FLOAT = -NEW_PI_FLOAT;
	public static float NEG_HALF_NEW_PI_FLOAT = -HALF_NEW_PI_FLOAT;
	public static float NEG_QUARTER_NEW_PI_FLOAT = -QUARTER_NEW_PI_FLOAT;
	public static float NEG_NEW_TAU_FLOAT = -NEW_TAU_FLOAT;


	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("getpi")
					.executes(context -> {
						context.getSource().sendFeedback(() -> Text.of("Pi is " + NEW_PI), false);
						return 1;
					})
			);
			dispatcher.register(CommandManager.literal("setpi")
					.then(CommandManager.argument("value", DoubleArgumentType.doubleArg())
							.executes(context -> {
								NEW_PI = DoubleArgumentType.getDouble(context, "value");
								updateValues();
								context.getSource().sendFeedback(() -> Text.of("Pi set to " + NEW_PI), false);
								return 1;
							})
					)
			);
		});
	}

	@SuppressWarnings("DuplicatedCode")
	public static void updateValues() {
		HALF_NEW_PI = NEW_PI / 2;
		QUARTER_NEW_PI = NEW_PI / 4;
		NEW_TAU = NEW_PI * 2;
		NEG_NEW_PI = -NEW_PI;
		NEG_HALF_NEW_PI = -HALF_NEW_PI;
		NEG_QUARTER_NEW_PI = -QUARTER_NEW_PI;
		NEG_NEW_TAU = -NEW_TAU;
		NEW_PI_FLOAT = (float) NEW_PI;
		HALF_NEW_PI_FLOAT = NEW_PI_FLOAT / 2;
		QUARTER_NEW_PI_FLOAT = NEW_PI_FLOAT / 4;
		NEW_TAU_FLOAT = NEW_PI_FLOAT * 2;
		NEG_NEW_PI_FLOAT = -NEW_PI_FLOAT;
		NEG_HALF_NEW_PI_FLOAT = -HALF_NEW_PI_FLOAT;
		NEG_QUARTER_NEW_PI_FLOAT = -QUARTER_NEW_PI_FLOAT;
		NEG_NEW_TAU_FLOAT = -NEW_TAU_FLOAT;
	}
}