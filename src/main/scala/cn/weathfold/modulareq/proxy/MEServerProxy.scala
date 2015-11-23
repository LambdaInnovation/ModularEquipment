package cn.weathfold.modulareq.proxy

import java.lang.reflect.Constructor

import cn.weathfold.modulareq.Registry
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item.ToolMaterial
import net.minecraft.item.{ItemPickaxe, ItemSpade, ItemSword, Item}

import scala.Option

class MEServerProxy {

  private class MyPickaxe(val mat: ToolMaterial) extends ItemPickaxe(mat) {}

  val creativeTabs: Map[String, CreativeTabs] = Map (
    "block" -> CreativeTabs.tabBlock,
    "decorations" -> CreativeTabs.tabDecorations,
    "redstone" -> CreativeTabs.tabRedstone,
    "transport" -> CreativeTabs.tabTransport,
    "misc" -> CreativeTabs.tabMisc,
    "food" -> CreativeTabs.tabFood,
    "tools" -> CreativeTabs.tabTools,
    "combat" -> CreativeTabs.tabCombat,
    "brewing" -> CreativeTabs.tabBrewing,
    "materials" -> CreativeTabs.tabMaterials
    )

  private def toolMat(name: String): ToolMaterial = {
    name match {
      case "wood" => ToolMaterial.WOOD
      case "stone" => ToolMaterial.STONE
      case "iron" => ToolMaterial.IRON
      case "emerald" => ToolMaterial.EMERALD
      case "diamond" => ToolMaterial.EMERALD
      case "gold" => ToolMaterial.GOLD
    }
  }

  // Common tasks goes here.

  def preInit() = {
    // Define types
    Registry.defineType("sword", (data) => new ItemSword(toolMat(data("toolClass").asInstanceOf[String])))
    Registry.defineType("shovel", (data) => new ItemSpade(toolMat(data("toolClass").asInstanceOf[String])))
    Registry.defineType("pickaxe", (data) => { new MyPickaxe(toolMat(data("toolClass").asInstanceOf[String])) })

    // CreativeTab task
    Registry.addTask("CreativeTab", (item, data, status) => {
      data.get("creativeTab") match {
        case Some(x: String) =>
          val c = creativeTabs(x)
          item.setCreativeTab(c)
          true
        case _ => false
      }
    }, 1)

    // Reg task
    Registry.addTask("Register", (item, data, status) => {
      GameRegistry.registerItem(item, data("name").asInstanceOf[String])
      true
    }, -1)
  }

}

object MEServerProxy {
  def assertStr(x: Any): String = x match {
      case y: String => y
      case _         => throw new RuntimeException("Invalid value " + x + ", expected string")
    }
}
