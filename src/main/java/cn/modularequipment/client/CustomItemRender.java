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
package cn.modularequipment.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IItemRenderer;

/**
 * @author WeAthFolD
 */
public class CustomItemRender implements IItemRenderer {
	
	public static class Transform {
		public float scale;
		public float[] offset;
		
		public Transform() {
			scale = 1.0f;
			offset = new float[] { 0, 0, 0 };
		}
		
		public Transform(float _scale, float[] delta) {
			scale = _scale;
			if(delta != null) {
				offset = new float[] { delta[0], delta[1], delta[2] };
			} else {
				offset = new float[] { 0, 0, 0 };
			}
		}
		
		public void transform() {
			GL11.glTranslatef(offset[0], offset[1], offset[2]);
			GL11.glScalef(scale, scale, scale);
		}
	}
	
	public Transform fp, tp;

	public CustomItemRender() {
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch(type) {
		case EQUIPPED_FIRST_PERSON:
			drawEquippedItem(item, fp);
			break;
		case EQUIPPED:
			drawEquippedItem(item, tp);
			break;
		default:
			break;
		}
	}
	
	public void drawEquippedItem(ItemStack stackToRender, Transform t) {
		IIcon icon = stackToRender.getIconIndex();
		
		Minecraft mc = Minecraft.getMinecraft();
		mc.renderEngine.bindTexture(mc.renderEngine.getResourceLocation(stackToRender.getItemSpriteNumber()));
		ResourceLocation tex = mc.renderEngine.getResourceLocation(stackToRender.getItemSpriteNumber());
		
		GL11.glPushMatrix();
		if(t != null) t.transform();
		drawEquippedItem(0.0625f, tex, tex, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), false);
		GL11.glPopMatrix();
	}
	
	private void addVertex(Vec3 vec, double u, double v) {
		Tessellator t = Tessellator.instance;
		t.addVertexWithUV(vec.xCoord, vec.yCoord, vec.zCoord, u, v);
	}
	
	static Vec3 vec(double x, double y, double z) {
		return Vec3.createVectorHelper(x, y, z);
	}
	
	private void drawEquippedItem(double w, ResourceLocation front, ResourceLocation back, 
			double u1, double v1, double u2, double v2, boolean faceOnly) {
		Vec3 a1 = vec(0, 0, w), 
			a2 = vec(1, 0, w), 
			a3 = vec(1, 1, w), 
			a4 = vec(0, 1, w), 
			a5 = vec(0, 0, -w), 
			a6 = vec(1, 0, -w), 
			a7 = vec(1, 1, -w), 
			a8 = vec(0, 1, -w);

		Minecraft mc = Minecraft.getMinecraft();
		TextureManager tm = mc.getTextureManager();

		Tessellator t = Tessellator.instance;
		GL11.glPushMatrix();
		
		tm.bindTexture(back);
		t.startDrawingQuads();
		t.setNormal(0.0F, 0.0F, 1.0F);
		addVertex(a1, u2, v2);
		addVertex(a2, u1, v2);
		addVertex(a3, u1, v1);
		addVertex(a4, u2, v1);
		t.draw();

		tm.bindTexture(front);
		t.startDrawingQuads();
		t.setNormal(0.0F, 0.0F, -1.0F);
		addVertex(a8, u2, v1);
		addVertex(a7, u1, v1);
		addVertex(a6, u1, v2);
		addVertex(a5, u2, v2);
		t.draw();

		int var7;
		float var8;
		double var9;
		float var10;
		
		/*
		 * Gets the width/16 of the currently bound texture, used to fix the
		 * side rendering issues on textures != 16
		 */
		int tileSize = 32;
		float tx = 1.0f / (32 * tileSize);
		float tz = 1.0f / tileSize;

		if(!faceOnly) {
			t.startDrawingQuads();
			t.setNormal(-1.0F, 0.0F, 0.0F);
			for (var7 = 0; var7 < tileSize; ++var7) {
				var8 = (float) var7 / tileSize;
				var9 = u2 - (u2 - u1) * var8 - tx;
				var10 = 1.0F * var8;
				t.addVertexWithUV(var10, 0.0D, -w, var9, v2);
				t.addVertexWithUV(var10, 0.0D, w, var9, v2);
				t.addVertexWithUV(var10, 1.0D, w, var9, v1);
				t.addVertexWithUV(var10, 1.0D, -w, var9, v1);
	
				t.addVertexWithUV(var10, 1.0D, w, var9, v1);
				t.addVertexWithUV(var10, 0.0D, w, var9, v2);
				t.addVertexWithUV(var10, 0.0D, -w, var9, v2);
				t.addVertexWithUV(var10, 1.0D, -w, var9, v1);
			}
			t.draw();
		}

		GL11.glPopMatrix();
	}
	

}
