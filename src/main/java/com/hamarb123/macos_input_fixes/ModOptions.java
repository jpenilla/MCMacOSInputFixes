package com.hamarb123.macos_input_fixes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.OptionInstance;
import org.apache.commons.io.IOUtils;
import com.hamarb123.macos_input_fixes.mixin.gui.GameOptionsAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class ModOptions
{
	//note: most of the comments for this code are at the top


	//here are the different methods that we target for different versions to create option instances:

	//1.19:
	/*
		SimpleOption(
			String key,
			SimpleOption.TooltipFactoryGetter<T> tooltipFactoryGetter,
			SimpleOption.ValueTextGetter<T> valueTextGetter,
			SimpleOption.Callbacks<T> callbacks,
			T defaultValue,
			Consumer<T> changeCallback)
	*/

	//1.18 or 1.17:
	/*
		Option - base type
		DoubleOption(
			String key,
			double min,
			double max,
			float step,
			Function<GameOptions,Double> getter,
			BiConsumer<GameOptions,Double> setter,
			BiFunction<GameOptions,DoubleOption,Text> displayStringGetter)
		static CyclingOption<Boolean> create(
			String key,
			Text on,
			Text off,
			Function<GameOptions,Boolean> getter,
			CyclingOption.Setter<Boolean> setter)
	*/

	//1.16:
	/*
		Same Option and DoubleOption
		CyclingOption(
			String key,
			BiConsumer<GameOptions,Integer> setter,
			BiFunction<GameOptions,CyclingOption,Text> messageProvider)
	*/

	//1.15 or 1.14:
	/*
		String instead of Text.
	*/

	//1.19 4th: SimpleOption.DoubleSliderCallbacks.INSTANCE
	//1.19 4th: SimpleOption.BOOLEAN

	//todo: update above comments to reflect changes from tooltips


	//here's the implementation for creating the different interface elements:

	private static Component createLiteralText(String value)
	{
		return Component.literal(value);
	}

	@SuppressWarnings("unchecked")
	private static OptionInstance<Double> doubleOption(String key, String prefix, double min, double max, float step, Supplier<Double> getter, Consumer<Double> setter, String tooltip)
	{
		try
		{
			//1.19+
			double step2 = (max - min) / step;
			OptionInstance.CaptionBasedToString<Double> valueTextGetterImpl = (optionText, value) ->
			{
				double result = Math.round(value * step2) * step + min;
				return (Component)createLiteralText(prefix + ": " + result);
			};

			Double defaultValue = (getter.get() - min) / (max - min);

			Consumer<Double> changeCallback = (value) ->
			{
				double result = Math.round(value * step2) * step + min;
				setter.accept(result);
				saveOptions();
			};
			//1.19.3+
			OptionInstance.TooltipSupplier<Double> tooltipParameter = createTooltip(true, tooltip, 1193);
			return FabricReflectionHelper.new_SimpleOption_2(key, tooltipParameter, valueTextGetterImpl, OptionInstance.UnitDouble.INSTANCE, defaultValue, changeCallback);
		}
		catch (Throwable t)
		{
			throw new RuntimeException("Failed to create the double option interface element.", t);
		}
	}

	private static OptionInstance<Boolean> booleanOption(String key, String prefix, Supplier<Boolean> getter, Consumer<Boolean> setter, String tooltip)
	{
		try
		{
			//1.19+
			OptionInstance.CaptionBasedToString<Boolean> valueTextGetterImpl = (optionText, value) ->
			{
				return (Component)createLiteralText(prefix + ": " + (value ? "ON" : "OFF"));
			};

			Consumer<Boolean> changeCallback = (value) ->
			{
				setter.accept(value);
				saveOptions();
			};

			OptionInstance<Boolean> returnValue;
			//1.19.3+
			OptionInstance.TooltipSupplier<Boolean> tooltipParameter = createTooltip(false, tooltip, 1193);
			returnValue = FabricReflectionHelper.new_SimpleOption_2(key, tooltipParameter, valueTextGetterImpl,  OptionInstance.BOOLEAN_VALUES, getter.get(), changeCallback);

			((OptionMixinHelper) (Object) returnValue).setOmitBuilderKeyText();
			return returnValue;
		}
		catch (Throwable t)
		{
			throw new RuntimeException("Failed to create the boolean option interface element.", t);
		}
	}

	//there doesn't seem to be a way to do tooltips on 1.14 and 1.15, so for now we won't

	private static <T> OptionInstance.TooltipSupplier<T> createTooltip(boolean isDouble, String tooltip, int version) throws Throwable
	{
		//version is in the following format: 1.19.3 = 1193
		//rounded down to what we know (e.g. 1.16.5 may be just 1160, or 1.15 may be just 1140)

		if (version >= 1193)
		{
			//1.19.3+
			return tooltip == null ? FabricReflectionHelper.SimpleOption_emptyTooltip_2() : FabricReflectionHelper.SimpleOption_constantTooltip_2(createLiteralText(tooltip));
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private static Object writeTooltip(Object option, Object tooltipObject, int version) throws Throwable
	{
		//version is in the following format: 1.19.3 = 1193
		//rounded down to what we know (e.g. 1.16.5 may be just 1160, or 1.15 may be just 1140)

		if (version >= 1190)
		{
			//unsupported for 1.19+
			throw new RuntimeException("writeTooltip is not implemented for 1.19+");
		}

		return option;
	}


	//here is the rest of the class that selects the interface options, and loads & stores the options:

	//return the list of interface options to show
	public static Object[] getModOptions()
	{
		loadInterface(); //load the elements if they are not loaded yet
		if (Minecraft.ON_OSX)
		{
			//on macOS show reverse scrolling, reverse hotbar scrolling, trackpad sensitivity, momentum scrolling, interface smooth scroll options, disable ctrl+click fix
			Object[] arr = new Object[6];
			arr[0] = REVERSE_SCROLLING;
			arr[1] = REVERSE_HOTBAR_SCROLLING;
			arr[2] = TRACKPAD_SENSITIVITY;
			arr[3] = MOMENTUM_SCROLLING;
			arr[4] = INTERFACE_SMOOTH_SCROLL;
			arr[5] = DISABLE_CTRL_CLICK_FIX;
			return arr;
		}
		else
		{
			//otherwise show reverse scrolling, and reverse hotbar scrolling options only
			Object[] arr = new Object[2];
			arr[0] = REVERSE_SCROLLING;
			arr[1] = REVERSE_HOTBAR_SCROLLING;
			return arr;
		}
	}

	private static boolean loadedInterface = false;
	private static void loadInterface()
	{
		//load the option instances if they are not already loaded
		if (loadedInterface) return;
		try
		{
			//this is only used on macOS, so only load it here so we don't accidentally call any of these on other platforms
			if (Minecraft.ON_OSX)
			{
				TRACKPAD_SENSITIVITY = doubleOption(
					"options.macos_input_fixes.trackpad_sensitivity",
					"Trackpad Sensitivity",
					0.0, 100.0, 1.0f,
					() -> trackpadSensitivity,
					(value) -> setTrackpadSensitivity(value),
					"The grouping feature only affects hotbar scrolling.\nThis feature only affects scrolling from the trackpad (and other high precision devices).\nDefault: 20.0\n0.0: Disable custom trackpad scroll processing.\nOther: group scrolls together to make scrolling speed much more reasonable on hotbar, scroll amount is divided by the value chosen here.");

				MOMENTUM_SCROLLING = booleanOption(
					"options.macos_input_fixes.momentum_scrolling",
					"Momentum Scrolling",
					() -> momentumScrolling,
					(value) -> setMomentumScrolling(value),
					"Only affects hotbar scrolling.\nA momentum scroll is when macOS keeps scrolling after you release the wheel.\nDefault: OFF\nOFF: ignore 'momentum scroll' events.\nON: process 'momentum scroll' events.");

				INTERFACE_SMOOTH_SCROLL = booleanOption(
					"options.macos_input_fixes.smooth_scroll",
					"Interface Smooth Scroll",
					() -> interfaceSmoothScroll,
					(value) -> setInterfaceSmoothScroll(value),
					"Affects all scrolling from legacy input devices (except for the hotbar).\nmacOS sometimes adjusts how much a single scroll does to make it feel 'smoother', but this can cause scroll amounts to feel random sometimes.\nDefault: OFF\nOFF: Modify smooth scrolling events to all be the same scroll amount.\nON: Keep smooth scrolling events as-is.");

				DISABLE_CTRL_CLICK_FIX = booleanOption(
					"options.macos_input_fixes.disable_ctrl_click_fix",
					"Disable Ctrl+Click Fix",
					() -> disableCtrlClickFix,
					(value) -> disableCtrlClickFix = value,
					"When enabled, disables the fix for the bug which causes Minecraft\nto map Control + Left Click to Right Click.");
			}

			REVERSE_HOTBAR_SCROLLING = booleanOption(
				"options.macos_input_fixes.reverse_hotbar_scrolling",
				"Reverse Hotbar Scroll",
				() -> reverseHotbarScrolling,
				(value) -> reverseHotbarScrolling = value,
				"Reverses the direction that scrolling goes for the hotbar when enabled.");

			REVERSE_SCROLLING = booleanOption(
				"options.macos_input_fixes.reverse_scrolling",
				"Reverse Scrolling",
				() -> reverseScrolling,
				(value) -> reverseScrolling = value,
				"Reverses the direction of all scrolling when enabled.");

			loadedInterface = true;
		}
		catch (Throwable t)
		{
			throw new RuntimeException("Failed to initialise option interface elements.", t);
		}
	}

	//saving and loading of options:

	public static File optionsFile;

	@SuppressWarnings("resource")
	public static void loadOptions()
	{
		//load options similarly to how minecraft does
		optionsFile = new File(Minecraft.getInstance().gameDirectory, "options_macos_input_fixes.txt");
		try
		{
			if (!optionsFile.exists())
			{
				return;
			}
			List<String> lines = IOUtils.readLines(new FileInputStream(optionsFile), StandardCharsets.UTF_8); //split by lines
			CompoundTag compoundTag = new CompoundTag();
			for (String line : lines) //read the lines into a tag
			{
				try
				{
					Iterator<String> iterator = GameOptionsAccessor.COLON_SPLITTER().omitEmptyStrings().limit(2).split(line).iterator();
					compoundTag.putString(iterator.next(), iterator.next());
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
			}
			if (compoundTag.contains("trackpadSensitivity")) //read trackpadSensitivity option
			{
				double actualValue = 20.0; //default value
				try
				{
					Double value = Double.parseDouble(compoundTag.getString("trackpadSensitivity"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				setTrackpadSensitivity(actualValue);
			}
			if (compoundTag.contains("reverseHotbarScrolling")) //read reverseHotbarScrolling option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("reverseHotbarScrolling"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				reverseHotbarScrolling = actualValue;
			}
			if (compoundTag.contains("reverseScrolling")) //read reverseScrolling option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("reverseScrolling"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				reverseScrolling = actualValue;
			}
			if (compoundTag.contains("momentumScrolling")) //read momentumScrolling option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("momentumScrolling"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				setMomentumScrolling(actualValue);
			}
			if (compoundTag.contains("interfaceSmoothScroll")) //read interfaceSmoothScroll option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("interfaceSmoothScroll"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				setInterfaceSmoothScroll(actualValue);
			}
			if (compoundTag.contains("disableCtrlClickFix")) //read disableCtrlClickFix option
			{
				boolean actualValue = false; //default value
				try
				{
					Boolean value = Boolean.parseBoolean(compoundTag.getString("disableCtrlClickFix"));
					actualValue = value;
				}
				catch (Exception ex1)
				{
					ex1.printStackTrace(System.err); //failed to parse
				}
				disableCtrlClickFix = actualValue;
			}

			loadedInterface = false;
		}
		catch (Exception ex2)
		{
			ex2.printStackTrace(System.err); //failed to do some sort of IO or something
		}
	}

	public static void saveOptions()
	{
		//write the options to the file in a similar way to minecraft
		try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(optionsFile), StandardCharsets.UTF_8)))
		{
			printWriter.println("trackpadSensitivity:" + trackpadSensitivity);
			printWriter.println("reverseHotbarScrolling:" + reverseHotbarScrolling);
			printWriter.println("reverseScrolling:" + reverseScrolling);
			printWriter.println("momentumScrolling:" + momentumScrolling);
			printWriter.println("interfaceSmoothScroll:" + interfaceSmoothScroll);
			printWriter.println("disableCtrlClickFix:" + disableCtrlClickFix);
		}
		catch (Exception ex2)
		{
			ex2.printStackTrace(System.err); //failed to do some sort of IO or something
		}
	}

	//todo: use lang files for the below

	//trackpad sensitivity option code:

	public static double trackpadSensitivity = 20.0;

	public static void setTrackpadSensitivity(double value)
	{
		trackpadSensitivity = value;

		//set the value in the native library also, ensure the value is clamped here
		if (value < 0) value = 0.0;
		else if (value > 100.0) value = 100.0;
		MacOSInputFixesClientMod.setTrackpadSensitivity(value);
	}

	public static Object TRACKPAD_SENSITIVITY;

	//other options code:

	public static boolean reverseHotbarScrolling = false;
	public static Object REVERSE_HOTBAR_SCROLLING;

	public static boolean reverseScrolling = false;
	public static Object REVERSE_SCROLLING;

	public static boolean momentumScrolling = false;
	public static Object MOMENTUM_SCROLLING;

	public static void setMomentumScrolling(boolean value)
	{
		momentumScrolling = value;

		//set the value in the native library also
		MacOSInputFixesClientMod.setMomentumScrolling(value);
	}

	public static boolean interfaceSmoothScroll = false;
	public static Object INTERFACE_SMOOTH_SCROLL;

	public static void setInterfaceSmoothScroll(boolean value)
	{
		interfaceSmoothScroll = value;

		//set the value in the native library also
		MacOSInputFixesClientMod.setInterfaceSmoothScroll(value);
	}

	public static boolean disableCtrlClickFix = false;
	public static Object DISABLE_CTRL_CLICK_FIX;
}
