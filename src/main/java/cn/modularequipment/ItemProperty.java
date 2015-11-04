package cn.modularequipment;

import cn.modularequipment.client.CustomItemRender.Transform;

// GSON Friendly
public class ItemProperty {
	
	public String
		name = null,
		type = "basic",
		toolClass = "wood",
		armorClass = "wood",
		creativeTab = null,
		texture = null;
	
	public Boolean full3D;
	public Transform fp_transform, tp_transform;

}
