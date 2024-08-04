package com.hamarb123.macos_input_fixes.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MouseInvokerMixin
{
	//allows the onMouseScroll method to be invoked since with these mappings it is private
	@Invoker("onScroll")
	public void callOnMouseScroll(long window, double horizontal, double vertical);
}
