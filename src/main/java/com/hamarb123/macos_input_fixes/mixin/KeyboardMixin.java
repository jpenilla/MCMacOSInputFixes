package com.hamarb123.macos_input_fixes.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.hamarb123.macos_input_fixes.Common;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin
{
	@Inject(at = @At("HEAD"), method = "keyPress(JIIII)V", cancellable = true)
	public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo info)
	{
		if (Minecraft.ON_OSX)
		{
			//disable built-in callback for tab and escape
			// - these are the keys which don't get registered properly when control is pressed in some configurations
			// - space can seemingly ONLY be fixed by changing macOS settings
			if (key == GLFW.GLFW_KEY_TAB || key == GLFW.GLFW_KEY_ESCAPE)
			{
				if (!Common.allowInputOSX2())
				{
					//only accept key event on macOS if it's from the native callback
					info.cancel();
					return;
				}
			}
		}
	}
}
