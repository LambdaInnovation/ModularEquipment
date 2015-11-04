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

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import cn.modularequipment.client.CustomItemRender;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * @author WeAthFolD
 */
@Mod(modid = "modular-equipment", name = "ModularEquipment", version = "0.11")
public class ModularEquipment {
	
	public static final Logger log = LogManager.getLogger("ModularEquipment");
	public static final boolean DEBUG = true;
	
	Map<String, CreativeTabs> cm = new HashMap();
	{
		cm.put("block",			CreativeTabs.tabBlock);
		cm.put("decorations", 	CreativeTabs.tabDecorations);
		cm.put("redstone", 		CreativeTabs.tabRedstone);
		cm.put("transport", 	CreativeTabs.tabTransport);
		cm.put("misc", 			CreativeTabs.tabMisc);
		cm.put("food", 			CreativeTabs.tabFood);
		cm.put("tools", 		CreativeTabs.tabTools);
		cm.put("combat", 		CreativeTabs.tabCombat);
		cm.put("brewing", 		CreativeTabs.tabBrewing);
		cm.put("materials", 	CreativeTabs.tabMaterials);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Gson gson = new GsonBuilder().create();
		
		File file = event.getSuggestedConfigurationFile();
		try {
			file.createNewFile();
			JsonArray elem = gson.fromJson(IOUtils.toString(new FileInputStream(file)), JsonArray.class);
			ItemProperty[] properties = gson.fromJson(elem, ItemProperty[].class);
			ItemArmor armor;
			
			boolean client = FMLCommonHandler.instance().getSide() == Side.CLIENT;
			
			for(ItemProperty p : properties) {
				Item item = null;
				List<Item> toreg = new ArrayList();
				CreativeTabs tab = null;
				switch(p.type) {
				case "basic":
					item = new Item();
					tab = CreativeTabs.tabMisc;
					break;
				case "sword":
					item = new ItemSword(toolMat(p.toolClass));
					tab = CreativeTabs.tabCombat;
					break;
				case "shovel":
					item = new ItemSpade(toolMat(p.toolClass));
					tab = CreativeTabs.tabTools;
					break;
				case "pickaxe":
					Constructor<ItemPickaxe> ctor = ItemPickaxe.class.getDeclaredConstructor(ToolMaterial.class);
					ctor.setAccessible(true);
					item = ctor.newInstance(toolMat(p.toolClass));
					tab = CreativeTabs.tabTools;
					break;
				case "axe":
					Constructor<ItemAxe> ctor2 = ItemAxe.class.getDeclaredConstructor(ToolMaterial.class);
					ctor2.setAccessible(true);
					item = ctor2.newInstance(toolMat(p.toolClass));
					tab = CreativeTabs.tabTools;
					break;
				case "armor":
					tab = CreativeTabs.tabCombat;
					for(int i = 0; i < 4; ++i) {
						toreg.add(new CustomArmor(armorMat(p.armorClass), 
								p.texture == null ? p.texture : p.name, 
								i));
					}
					break;
				default:
					err("Invalid type " + p.type);
				}
				
				// CreativeTab override
				if(p.creativeTab != null) {
					tab = cm.get(p.creativeTab);
					if(tab == null)
						err("Invalid cct name " + p.creativeTab);
				}
				
				if(client) {
					loadClient(p, item);
				}
				
				if(toreg.isEmpty()) {
					toreg.add(item);
				}
				
				proc(toreg, tab, p);
			}
		} catch(Exception e) {
			log.error("Error loading info from config", e);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void loadClient(ItemProperty p, Item item) {
		if(p.full3D != null && p.full3D) {
			item.setFull3D();
		}
		
		if(p.fp_transform != null || p.tp_transform != null) {
			log.info("Registered custom renderer for " + p.name);
			CustomItemRender renderer = new CustomItemRender();
			renderer.fp = p.fp_transform;
			renderer.tp = p.tp_transform;
			MinecraftForgeClient.registerItemRenderer(item, renderer);
		}
	}
	
	private void err(String reason) {
		throw new RuntimeException(reason);
	}
	
	private void proc(List<Item> list, CreativeTabs tab, ItemProperty p) {
		if(p.name == null)
			err("You must specify the item's name");
		if(list.size() == 1) {
			procSingle(list.get(0), p.name, tab, p);
			return;
		}
		
		int i = 0;
		for(Item item : list) {
			procSingle(item, p.name + "_" + (i++), tab, p);
		}
	}
	
	private void procSingle(Item item, String id, CreativeTabs tab, ItemProperty p) {
		item.setUnlocalizedName(id);
		item.setCreativeTab(tab);
		if(p.texture != null)
			item.setTextureName(p.texture);
		GameRegistry.registerItem(item, id);
		log.info("Registered item " + p.name + "/" + item + "/" + item.getCreativeTab());
	}
	
	private void debug(String txt) {
		if(DEBUG)
			log.info("[DEBUG]" + txt);
	}
	
	private ToolMaterial toolMat(String name) {
		if(name == null)
			err("You must specify the tool material.");
		switch(name) {
		case "wood":
			return ToolMaterial.WOOD;
		case "stone":
			return ToolMaterial.STONE;
		case "iron":
			return ToolMaterial.IRON;
		case "emerald":
		case "diamond":
			return ToolMaterial.EMERALD;
		case "gold":
			return ToolMaterial.GOLD;
		}
		err("No such material " + name);
		return null; // NEVER reach
	}
	
	private ArmorMaterial armorMat(String name) {
		if(name == null)
			err("You must specify the armor material.");
		switch(name) {
		case "cloth": return ArmorMaterial.CLOTH;
		case "chain": return ArmorMaterial.CHAIN;
		case "iron": return ArmorMaterial.IRON;
		case "gold": return ArmorMaterial.GOLD;
		case "diamond": return ArmorMaterial.DIAMOND;
		}
		err("No such armor material " + name);
		return null; // NEVER reach
	} 
	
}
 