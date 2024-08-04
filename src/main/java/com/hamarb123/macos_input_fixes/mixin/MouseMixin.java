package com.hamarb123.macos_input_fixes.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.hamarb123.macos_input_fixes.Common;
import com.hamarb123.macos_input_fixes.ModOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public class MouseMixin
{
	@Inject(at = @At("HEAD"), method = "onScroll(JDD)V", cancellable = true)
	private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info)
	{
		if (Minecraft.ON_OSX)
		{
			if (vertical == 0)
			{
				//if vertical is 0 then there is no scroll
				info.cancel();
				return;
			}
			if (!Common.allowInputOSX())
			{
				//only accept scroll event on macOS if it's from the native callback
				info.cancel();
				return;
			}
		}
	}

	@ModifyVariable(method = "onScroll(JDD)V", at = @At("HEAD"), ordinal = 0)
	private double maybeReverseHScroll(double value)
	{
		//if the reverse scrolling option is enabled, reverse the horizontal scroll value
		return ModOptions.reverseScrolling ? -value : value;
	}

	@ModifyVariable(method = "onScroll(JDD)V", at = @At("HEAD"), ordinal = 1)
	private double maybeReverseVScroll(double value)
	{
		//if the reverse scrolling option is enabled, reverse the vertical scroll value
		return ModOptions.reverseScrolling ? -value : value;
	}

	@Redirect(method = "onPress(JIII)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ON_OSX:Z", opcode = Opcodes.GETSTATIC))
	private boolean leftMouseClick()
	{
		//check if we want to disable the below fix
		if (ModOptions.disableCtrlClickFix) return Minecraft.ON_OSX;

		//onMouseButton converts left click + control on macOS to right click, simply tell it we're not on macOS so it can't do that (there's no other uses of macOS in the function)
		return false;
	}
}
