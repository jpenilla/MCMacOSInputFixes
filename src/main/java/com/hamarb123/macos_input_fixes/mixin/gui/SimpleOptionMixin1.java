package com.hamarb123.macos_input_fixes.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.hamarb123.macos_input_fixes.Common;
import com.hamarb123.macos_input_fixes.OptionMixinHelper;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;

@Mixin(OptionInstance.class)
public class SimpleOptionMixin1 implements OptionMixinHelper
{
	private boolean omitBuilderKeyText = false;

	@Inject(method = "createButton(Lnet/minecraft/client/Options;III)Lnet/minecraft/client/gui/components/AbstractWidget;", at = @At("HEAD"))
	private void before_createWidget(Options options, int x, int y, int width, CallbackInfoReturnable<?> info)
	{
		if (omitBuilderKeyText)
		{
			//set the flag for so that when the callee creates the widget it has the correct text
			Common.setOmitBuilderKeyText(true);
		}
	}

	@Inject(method = "createButton(Lnet/minecraft/client/Options;III)Lnet/minecraft/client/gui/components/AbstractWidget;", at = @At("RETURN"))
	private void after_createWidget(Options options, int x, int y, int width, CallbackInfoReturnable<?> info)
	{
		if (omitBuilderKeyText)
		{
			//unset the flag for so that when the next callee creates its widget it works how it should
			Common.setOmitBuilderKeyText(false);
		}
	}

	@Override
	public void setOmitBuilderKeyText()
	{
		//this method implements the OptionMixinHelper interface
		//store whether omitKeyText() should be called
		omitBuilderKeyText = true;
	}
}
