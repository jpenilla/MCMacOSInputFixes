package com.hamarb123.macos_input_fixes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.hamarb123.macos_input_fixes.ModOptions;
import net.minecraft.world.entity.player.Inventory;

@Mixin(Inventory.class)
public class PlayerInventoryMixin
{
	@ModifyVariable(method = "swapPaint(D)V", at = @At("HEAD"), ordinal = 0)
	private double fixHotbarScrollDirection(double d)
	{
		//if the reverse hotbar scrolling option is enabled, reverse the scroll value
		return ModOptions.reverseHotbarScrolling ? -d : d;
	}
}
