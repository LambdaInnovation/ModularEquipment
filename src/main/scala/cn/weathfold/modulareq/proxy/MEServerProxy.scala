package cn.weathfold.modulareq.proxy

import java.lang.reflect.Constructor

import cn.weathfold.modulareq.DelegateClass.CustomArmor
import cn.weathfold.modulareq.Registry
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.item.Item.ToolMaterial
import net.minecraft.item.ItemArmor.ArmorMaterial
import net.minecraft.item._

import scala.Option

class MEServerProxy {

  private class MyPickaxe(val mat: ToolMaterial) extends ItemPickaxe(mat)

  private class MyAxe(val mat: ToolMaterial) extends ItemAxe(mat)

  private class CustomArmor(val mat: ArmorMaterial, val name: String, val icon: String,
                    val texture: String, val armorType: Int)
    extends ItemArmor(mat, 0, armorType) {

    setTextureName(icon + "_" + armorType)

    override def getArmorTexture(stack: ItemStack, entity: Entity, slot: Int, layer: String): String =
      if (armorType == 2) texture + "_0" else texture + "_1"

  }

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

  private def armorMat(name: String): ArmorMaterial = {
    name match {
      case "cloth"   => ArmorMaterial.CLOTH
      case "chain"   => ArmorMaterial.CHAIN
      case "iron"    => ArmorMaterial.IRON
      case "gold"    => ArmorMaterial.GOLD
      case "diamond" => ArmorMaterial.DIAMOND
    }
  }

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

  private def toolMatFromData(data: Map[String, Any]) = toolMat(data("toolClass").asInstanceOf[String])
  private def armorMatFromData(data: Map[String, Any]) = armorMat(data("armorClass").asInstanceOf[String])

  // Common tasks goes here.

  def preInit() = {
    // Define types
    Registry.defineType("sword", data => new ItemSword(toolMatFromData(data)))
    Registry.defineType("shovel", data => new ItemSpade(toolMatFromData(data)))
    Registry.defineType("pickaxe", data => new MyPickaxe(toolMatFromData(data)))
    Registry.defineType("axe", data => new MyAxe(toolMatFromData(data)))
    Registry.defineType("armor", data => {
      val armorType = data("armorType").asInstanceOf[Int]
      val name = data("name").asInstanceOf[String]
      val icon = if(data("icon") == null) name else data("icon").asInstanceOf[String]
      val texture = if(data("armorTexture") == null) name else data("armorTexture").asInstanceOf[String]

      new CustomArmor(armorMatFromData(data), name, icon, texture, armorType)
      },
      element => (1 until 4).map(x => element updated ("armorType", x))
    )

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

    // Armor task
    Registry.addTaskOfType("armor", "RegArmor", (item, data, status) => {
      GameRegistry.registerItem(item, data("name").asInstanceOf[String] + data("armorType"))
      status.markExec("Register")
      status.markExec("Icon")
      true
    }, -9)

    // Reg task
    Registry.addTask("Register", (item, data, status) => {
      GameRegistry.registerItem(item, data("name").asInstanceOf[String])
      true
    }, -10)
  }

}
