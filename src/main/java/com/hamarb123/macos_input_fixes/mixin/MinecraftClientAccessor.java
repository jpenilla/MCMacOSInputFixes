package com.hamarb123.macos_input_fixes.mixin;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor
{
	//allows the window field to be accessed since with these mappings it is private
	//we could use the getWindow() function, but it is only available in 1.15+
	@Accessor
	Window getWindow();

	@Accessor
	Font getFont();
}
