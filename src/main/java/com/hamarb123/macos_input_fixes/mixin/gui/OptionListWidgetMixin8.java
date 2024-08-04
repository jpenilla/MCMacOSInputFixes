package com.hamarb123.macos_input_fixes.mixin.gui;

import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.hamarb123.macos_input_fixes.ModOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.OptionsList;

@Mixin(OptionsList.class)
public class OptionListWidgetMixin8
{
	@Shadow @Final private OptionsSubScreen screen;

	//this is where we add additional menu options
	@ModifyVariable(method = "addSmall([Lnet/minecraft/client/OptionInstance;)V", at = @At("HEAD"), ordinal = 0)
	private OptionInstance<?>[] modifyAddAllParameter1(OptionInstance<?>[] options)
	{
		//check if it's a MouseOptionsScreen, otherwise we don't want to modify
		if (!(screen instanceof MouseSettingsScreen)) return options;

		//get the mod options so we can add them to the game options
		Object[] modOptions = ModOptions.getModOptions();
		if (modOptions == null) return options;

		//combine the game options and mod options
		OptionInstance<?>[] newOptions = new OptionInstance<?>[options.length + modOptions.length];
		for (int i = 0; i < options.length; i++) newOptions[i] = options[i];
		for (int i = 0; i < modOptions.length; i++) newOptions[options.length + i] = (OptionInstance<?>)modOptions[i];
		return newOptions;
	}
}
