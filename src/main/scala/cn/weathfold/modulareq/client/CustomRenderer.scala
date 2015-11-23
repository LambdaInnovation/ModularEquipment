package cn.weathfold.modulareq.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.item.ItemStack
import net.minecraft.util.{Vec3, IIcon}
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.{ItemRendererHelper, ItemRenderType}

import org.lwjgl.opengl.GL11._

class Transform(val scale: Float = 1.0f, val offset: Vector[Float] = new Vector[Float](3)) {

  def transform() = {
    glTranslatef(offset(0), offset(1), offset(2))
    glScalef(scale, scale, scale)
  }

}

class CustomRenderer(val fp: Transform = new Transform, val tp: Transform = new Transform) extends IItemRenderer {

  override def handleRenderType(item: ItemStack, renderType: ItemRenderType): Boolean =
    renderType == ItemRenderType.EQUIPPED || renderType == ItemRenderType.EQUIPPED_FIRST_PERSON

  override def shouldUseRenderHelper(irt: ItemRenderType, stack: ItemStack, helper: ItemRendererHelper) = false

  override def renderItem(irt: ItemRenderType, stack: ItemStack, data: Object*) = {
    val mc = Minecraft.getMinecraft
    irt match {
      case ItemRenderType.EQUIPPED_FIRST_PERSON =>
        glPushMatrix()

        glPopMatrix()

      case ItemRenderType.EQUIPPED =>
        glPushMatrix()

        drawEquippedItem(stack)
        glPopMatrix()

      case _ =>
    }
  }

  private def vec(x: Double = 0, y: Double = 0, z: Double = 0): Vec3 = Vec3.createVectorHelper(x, y, z)

  private def addVertex(vec: Vec3, u: Double, v: Double) = {
    Tessellator.instance.addVertexWithUV(vec.xCoord, vec.yCoord, vec.zCoord, u, v)
  }

  private def drawEquippedItem(stack: ItemStack): Unit = {
    val icon = stack.getIconIndex
    val u1 = icon.getMinU(); val u2 = icon.getMaxU; val v1 = icon.getMinV; val v2 = icon.getMaxV
    val mc = Minecraft.getMinecraft
    val tm = mc.getTextureManager
    val t = Tessellator.instance
    val w = 0.0625

    val a1 = vec(0, 0, w)
    val a2 = vec(1, 0, w)
    val a3 = vec(1, 1, w)
    val a4 = vec(0, 1, w)
    val a5 = vec(0, 0, -w)
    val a6 = vec(1, 0, -w)
    val a7 = vec(1, 1, -w)
    val a8 = vec(0, 1, -w)

    tm.bindTexture(mc.renderEngine.getResourceLocation(stack.getItemSpriteNumber))

    t.startDrawingQuads()
    t.setNormal(0, 0, 1)
    addVertex(a1, u2, v2)
    addVertex(a2, u1, v2)
    addVertex(a3, u1, v1)
    addVertex(a4, u2, v1)
    t.draw()

    t.startDrawingQuads()
    t.setNormal(0, 0, -1)
    addVertex(a8, u2, v1)
    addVertex(a7, u1, v1)
    addVertex(a6, u1, v2)
    addVertex(a5, u2, v2)
    t.draw()

    val tileSize = 32
    val tx = 1.0f / (32 * tileSize)

    t.startDrawingQuads()
    t.setNormal(-1, 0, 0)
    for (var7 <- 0 until tileSize) {
      val var8 = var7.toFloat / tileSize
      val var9 = u2 - (u2 - u1) * var8 - tx
      val var10 = 1.0F * var8
      t.addVertexWithUV(var10, 0.0D, -w, var9, v2)
      t.addVertexWithUV(var10, 0.0D, w, var9, v2)
      t.addVertexWithUV(var10, 1.0D, w, var9, v1)
      t.addVertexWithUV(var10, 1.0D, -w, var9, v1)

      t.addVertexWithUV(var10, 1.0D, w, var9, v1)
      t.addVertexWithUV(var10, 0.0D, w, var9, v2)
      t.addVertexWithUV(var10, 0.0D, -w, var9, v2)
      t.addVertexWithUV(var10, 1.0D, -w, var9, v1)
    }
    t.draw()
  }
}
