package com.hamarb123.macos_input_fixes;

import java.util.function.Consumer;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FabricReflectionHelper {
	//Methods:

	/**
	 * <p>Intemediary names: {@code method_25441} (1.16+), {@code hasControlDown} (1.14-1.15.x)</p>
	 * <p>Mapped name: {@code hasControlDown}</p>
	 * <p>Containing class: {@code net.minecraft.class_437} ({@code net.minecraft.client.gui.screen.Screen})</p>
	 * <p>Descriptor: {@code ()Z}</p>
	 * <p>Return type: {@code boolean}</p>
	 * <p>Parameters types: (none)</p>
	 * <p>Static: yes</p>
	 * <p>Versions: 1.14+</p>
	 */
	public static boolean Screen_hasControlDown() {
		return Screen.hasControlDown();
	}

	/**
	 * <p>Intemediary name: {@code method_41753}</p>
	 * <p>Mapped name: {@code getValue}</p>
	 * <p>Containing class: {@code net.minecraft.class_7172} ({@code SimpleOption})</p>
	 * <p>Descriptor: {@code ()Ljava/lang/Object;}</p>
	 * <p>Return type: {@code Object (T)}</p>
	 * <p>Parameters types: ()</p>
	 * <p>Static: no</p>
	 * <p>Versions: 1.19+</p>
	 */
	public static <T> T SimpleOption_getValue(OptionInstance<T> instance) {
		return instance.get();
	}

	/**
	 * <p>Intemediary name: {@code method_42717}</p>
	 * <p>Mapped name: {@code constantTooltip}</p>
	 * <p>Containing class: {@code net.minecraft.class_7172} ({@code SimpleOption})</p>
	 * <p>Descriptor: {@code (Lnet/minecraft/class_2561;)Lnet/minecraft/class_7172$class_7277;}</p>
	 * <p>Return type: {@code SimpleOption.TooltipFactory}</p>
	 * <p>Parameters types: (Text text)</p>
	 * <p>Static: yes</p>
	 * <p>Versions: 1.19.3+</p>
	 */
	public static <T> OptionInstance.TooltipSupplier<T> SimpleOption_constantTooltip_2(Component text) {
		return OptionInstance.cachedConstantTooltip(text);
	}

	/**
	 * <p>Intemediary name: {@code method_42399}</p>
	 * <p>Mapped name: {@code emptyTooltip}</p>
	 * <p>Containing class: {@code net.minecraft.class_7172} ({@code SimpleOption})</p>
	 * <p>Descriptor: {@code ()Lnet/minecraft/class_7172$class_7277;}</p>
	 * <p>Return type: {@code SimpleOption.TooltipFactory}</p>
	 * <p>Parameters types: ()</p>
	 * <p>Static: yes</p>
	 * <p>Versions: 1.19.3+</p>
	 */
	public static <T> OptionInstance.TooltipSupplier<T> SimpleOption_emptyTooltip_2() {
		return OptionInstance.noTooltip();
	}

	/**
	 * <p>Intemediary name: {@code method_42439}</p>
	 * <p>Mapped name: {@code getDiscreteMouseScroll}</p>
	 * <p>Containing class: {@code net.minecraft.class_315} ({@code GameOptions})</p>
	 * <p>Descriptor: {@code ()Lnet/minecraft/class_7172;}</p>
	 * <p>Return type: {@code SimpleOption}</p>
	 * <p>Parameters types: ()</p>
	 * <p>Static: no</p>
	 * <p>Versions: 1.19+</p>
	 */
	public static OptionInstance<Boolean> GameOptions_getDiscreteMouseScroll(Options instance) {
		return instance.discreteMouseScroll();
	}

	//Constructors:

	/**
	 * <p>Class intemediary name: {@code net.minecraft.class_7172}</p>
	 * <p>Class mapped name: {@code net.minecraft.client.option.SimpleOption}</p>
	 * <p>Constructor descriptor: {@code (Ljava/lang/String;Lnet/minecraft/class_7172$class_7277;Lnet/minecraft/class_7172$class_7303;Lnet/minecraft/class_7172$class_7178;Ljava/lang/Object;Ljava/util/function/Consumer;)V}</p>
	 * <p>Parameters types: {@code (String key, SimpleOption.TooltipFactory<T> tooltipFactory, SimpleOption.ValueTextGetter<T> valueTextGetter, SimpleOption.Callbacks<T> callbacks, T defaultValue, Consumer<T> changeCallback)}</p>
	 * <p>Versions: 1.19.3+</p>
	 */
	@SuppressWarnings("unchecked")
	public static <T> OptionInstance<T> new_SimpleOption_2(
			String key,
			OptionInstance.TooltipSupplier<T> tooltipFactory,
			OptionInstance.CaptionBasedToString<T> valueTextGetter,
			Object valueSet,
			T defaultValue,
			Consumer<T> changeCallback
	) {
		try {
			return OptionInstance.class.getDeclaredConstructor(
					String.class,
					OptionInstance.TooltipSupplier.class,
					OptionInstance.CaptionBasedToString.class,
					Class.forName("net.minecraft.client.OptionInstance.ValueSet"),
					Object.class,
					Consumer.class
			).newInstance(key, tooltipFactory, valueTextGetter, valueSet, defaultValue, changeCallback);
		} catch (final ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
