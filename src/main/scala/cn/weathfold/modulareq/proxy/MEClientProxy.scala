package cn.weathfold.modulareq.proxy

import cn.weathfold.modulareq.Registry
import cn.weathfold.modulareq.client.{CustomRenderer, Transform}
import net.minecraft.item.Item
import net.minecraftforge.client.MinecraftForgeClient

class MEClientProxy extends MEServerProxy {

  override def preInit() = {
    super.preInit()

    def getTransform(data: Map[String, Any]): Transform = {
      var scale = 1.0f
      var offset = Vector(0f, 0f, 0f)
      data.foreach({
        case ("scale", s: Double) => scale = s.toFloat
        case ("transform", t: List[Double]) => offset = Vector(t(0).toFloat, t(1).toFloat, t(2).toFloat)
      })
      new Transform(scale, offset)
    }

    Registry.addTask("CustomRender", (item, data, status) => {
      var fpTransform: Transform = null
      var tpTransform: Transform = null
      data.get("customRender") match {
        case Some(rdata: Map[String, Any]) =>
          rdata.foreach({
              case ("full3D", b: Boolean) =>
                if(b) { item.setFull3D() }
              case ("fp_transform", data: Map[String, Any]) =>
                fpTransform = getTransform(data)
              case ("tp_transform", data: Map[String, Any]) =>
                tpTransform = getTransform(data)
              case _ =>
            })
          if(fpTransform == null) fpTransform = new Transform()
          if(tpTransform == null) tpTransform = new Transform()
          val renderer = new CustomRenderer(fpTransform, tpTransform)
          MinecraftForgeClient.registerItemRenderer(item, renderer)
          true
        case _ => false
      }
    })

    Registry.addTask("Icon", (item, data, status) => {
      val icon: String = (if(data("icon") == null) data("name") else data("icon")).asInstanceOf[String]
      item.setTextureName(icon)
      true
    })
  }



}
