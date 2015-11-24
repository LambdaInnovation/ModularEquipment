package cn.weathfold.modulareq

import java.io.FileReader

import cn.weathfold.modulareq.proxy.MEServerProxy
import cpw.mods.fml.common.{SidedProxy, Mod}
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.{LogManager, Logger}

import scala.util.parsing.json.JSON

@Mod(modid = "ModularEquipment", name = "ModularEquipment", version = ModularEquipment.version, modLanguage = "scala")
object ModularEquipment {

  val log = LogManager.getLogger("ModularEquipment")

  val debug = true

  final val version = "0.2_dev"

  @SidedProxy(clientSide = "cn.weathfold.modulareq.proxy.MEClientProxy",
    serverSide = "cn.weathfold.modulareq.proxy.MEServerProxy")
  var proxy: MEServerProxy = null

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) = {
    proxy.preInit()

    val str = IOUtils.toString(new FileReader(event.getSuggestedConfigurationFile))
    val list = JSON.parseFull(str) match {
      case Some(x: List[Map[String, Any]]) => x
      case _ => throw new RuntimeException("Invalid json string format")
    }

    Registry.construct(list)
  }

  @EventHandler
  def init(event: FMLInitializationEvent) = {

  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) = {

  }

}
