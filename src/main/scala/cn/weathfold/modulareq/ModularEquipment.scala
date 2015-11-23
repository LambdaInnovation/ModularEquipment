package cn.weathfold.modulareq

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import org.apache.logging.log4j.{LogManager, Logger}

@Mod(modid = "ModularEquipment", name = "ModularEquipment", version = ModularEquipment.version, modLanguage = "scala")
object ModularEquipment {

  val log = LogManager.getLogger("ModularEquipment")

  val debug = true

  final val version = "0.2_dev"

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) = {

  }

  @EventHandler
  def init(event: FMLInitializationEvent) = {

  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) = {

  }

}
