package cn.weathfold.modulareq.proxy

import cn.weathfold.modulareq.Registry
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item

import scala.Option

class MEServerProxy {

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

  // Common tasks goes here.

  def preInit() = {
    // Define types

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
  }

}

object MEServerProxy {
  def assertStr(x: Any): String = x match {
      case y: String => y
      case _         => throw new RuntimeException("Invalid value " + x + ", expected string")
    }
}
