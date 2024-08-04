package com.hamarb123.macos_input_fixes;

import java.io.IOException;
import net.minecraft.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = "macos_input_fixes", dist = Dist.CLIENT)
public class MacOSInputFixesClientMod
{
	public MacOSInputFixesClientMod()
	{
		ModOptions.loadOptions();
	}

	//these functions are defined in Objective C++
	public static native void registerCallbacks(ScrollCallback scrollCallback, KeyCallback keyCallback, long window);
	public static native void setTrackpadSensitivity(double sensitivity);
	public static native void setMomentumScrolling(boolean option);
	public static native void setInterfaceSmoothScroll(boolean option);

	static
	{
		if (Util.getPlatform() == Util.OS.OSX)
		{
			try
			{
				//load the Objective C++ function's library
				NativeUtils.loadLibraryFromJar("/natives/macos_input_fixes.dylib");
			}
			catch (IOException e2)
			{
				//uncomment below line and replace with project path if it fails to load from jar e.g. you're running in an ide. also comment the throw line if you do this
				//System.load("<path to project>/native/macos_input_fixes.dylib");
				e2.printStackTrace();
				throw new RuntimeException(e2);
			}
		}
	}
}
