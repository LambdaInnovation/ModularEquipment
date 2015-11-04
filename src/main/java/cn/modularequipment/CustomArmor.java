/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.li-dev.cn/
 *
 * This project is open-source, and it is distributed under
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * 本项目是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.modularequipment;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Stupid Minecraft
 * @author WeAthFolD
 */
public class CustomArmor extends ItemArmor {

	final String texture;
	
	public CustomArmor(ArmorMaterial material, String icon, String _texture, int armorType) {
		super(material, 0, armorType);
		setTextureName(icon + "_" + armorType);
		texture = _texture;
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String layer) {
		if (((ItemArmor) stack.getItem()).armorType == 2) {
			return texture + "_0";
		} else {
			return texture + "_1";
		}
	}

}
