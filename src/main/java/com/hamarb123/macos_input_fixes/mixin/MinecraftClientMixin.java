package com.hamarb123.macos_input_fixes.mixin;

import org.lwjgl.glfw.GLFWNativeCocoa;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.hamarb123.macos_input_fixes.Common;
import com.hamarb123.macos_input_fixes.FabricReflectionHelper;
import com.hamarb123.macos_input_fixes.MacOSInputFixesClientMod;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;

@Mixin(Minecraft.class)
public class MinecraftClientMixin
{
	@Final @Shadow private Window window;
	@Shadow @Final public KeyboardHandler keyboardHandler;
	@Shadow @Final public MouseHandler mouseHandler;
	@Final @Shadow public Options options;

	private boolean runOnce = false;

	//function that is called immediately after the window is created on both versions
	@Inject(at = @At("HEAD"), method = "setWindowActive(Z)V", cancellable = true)
	private void onWindowFocusChanged(boolean focused, CallbackInfo info)
	{
		if (Minecraft.ON_OSX)
		{
			if (!runOnce)
			{
				//register the native callback for scrolling
				long glfwWindow = window.getWindow();
				long cocoaWindow = GLFWNativeCocoa.glfwGetCocoaWindow(glfwWindow);
				MacOSInputFixesClientMod.registerCallbacks(this::scrollCallback, this::keyCallback, cocoaWindow);
				runOnce = true;
			}
		}
	}

	private void scrollCallback(double horizontal, double vertical, double horizontalWithMomentum, double verticalWithMomentum, double horizontalUngrouped, double verticalUngrouped)
	{
		//recieve the native scrolling callback & convert it into a scroll event

		//determine if discrete scroll is enabled
		boolean discreteScroll = (boolean)(Boolean)FabricReflectionHelper.SimpleOption_getValue(FabricReflectionHelper.GameOptions_getDiscreteMouseScroll(options)); //1.19+

		//replace ungrouped values with grouped values if discrete scroll is enabled
		if (discreteScroll)
		{
			horizontalUngrouped = horizontalWithMomentum;
			verticalUngrouped = verticalWithMomentum;
		}

		//use ungrouped values if not scrolling on hotbar
		if (((Minecraft)(Object)this).getOverlay() != null || ((Minecraft)(Object)this).screen != null || ((Minecraft)(Object)this).player == null)
		{
			horizontal = horizontalUngrouped;
			vertical = verticalUngrouped;
		}

		//combine vertical & horizontal here since it's harder to do in the actual method (when scrolling for hotbar)
		else
		{
			vertical += horizontal;
			horizontal = 0;
		}

		//check if we actually have an event still
		if (horizontal == 0 && vertical == 0) return;

		//enable onMouseScroll
		Common.setAllowedInputOSX(true);

		//on 1.14 we need to use the window field, on 1.19 the field still exists
		((MouseInvokerMixin)mouseHandler).callOnMouseScroll(((MinecraftClientAccessor)Minecraft.getInstance()).getWindow().getWindow(), horizontal, vertical);

		//disable onMouseScroll
		Common.setAllowedInputOSX(false);
	}

	private void keyCallback(int key, int scancode, int action, int modifiers)
	{
		//enable onKey
		Common.setAllowedInputOSX2(true);

		//on 1.14 we need to use the window field, on 1.19 the field still exists
		keyboardHandler.keyPress(((MinecraftClientAccessor)Minecraft.getInstance()).getWindow().getWindow(), key, scancode, action, modifiers);

		//disable onKey
		Common.setAllowedInputOSX2(false);
	}

	//dropping stack in game
	@Inject(method = "handleKeybinds()V", at = @At("HEAD"))
	private void keyPressed_hasControlDownBegin(CallbackInfo info)
	{
		//enable hasControlDown() injector
		Common.setInjectHasControlDown(true);
	}
	@Inject(method = "handleKeybinds()V", at = @At("RETURN"))
	private void keyPressed_hasControlDownEnd(CallbackInfo info)
	{
		//disable hasControlDown() injector
		Common.setInjectHasControlDown(false);
	}
}
