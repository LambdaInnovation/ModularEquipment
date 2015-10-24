ModularEquipment 说明文档
---

当前版本：0.1

ModularEquipment（以下简称ME）是一个通过读取配置文件从而动态载入各种类型武器的小型mod。

ME使用[JSON](http://www.json.org/)作为它的配置文件格式，并且允许通过修改配置文件添加一系列各种属性的物品。

接下来将对ME的通常使用方法和支持的物品类型做一个说明。


基本用法
---

ME的配置文件路径为`config/modular-equipment.cfg`。其格式应该如下：
```json
[obj1, obj2, obj3, ...]
```
其中每一个obj代表一个JSON对象，形如`{...}`。
每一个obj的数据都会被解析，并且创建**一个或多个**物品。关于创建的规则将在下面说明。


name属性
---

name是每一个obj中必须包含的属性。它有两个含义：

* 该物品的语言文件使用的键名称
* 当没有指定该物品的贴图时，默认使用的贴图名称

每一个obj始终应当指定一个name属性，否则加载将报错。


texture属性
---

texture属性指定了该物品的贴图路径。它应当形如`[namespace]:[texture-location]`，或`[texture-location]`(等价于`minecraft:[texture-location]`)


creativeTab属性
---
creativeTab属性指示了该物品所属的创造模式物品栏。默认情况下，所属物品栏由创建物品的类型所决定（e.g.剑位于战斗物品栏）。
它可以是以下几个值中的一个：

* "block"
* "decorations"
* "redstone"
* "transport"
* "misc"
* "food"
* "tools"
* "combat"
* "brewing"
* "materials"
		
		
type属性和各属性简介
---

type指示了该物品的类型（剑，铲子，盔甲，……）。某些类型可能需要读取一些附加的属性。

## basic类型

没有任何作用的占位物品。没有附加属性。

## sword, pickaxe, shovel, axe类型

各种工具类型。

附加属性：

* toolClass 工具的等级。可以是以下一系列值中的一个：
  * wood
  * stone
  * emerald
  * diamond (和emerald等价)
  * gold
  
## armor类型

盔甲物品。一个obj会对应创建一整套盔甲（头盔 胸甲 裤子 鞋子）。

附加属性

* armorClass 盔甲的等级。可以是一下一系列值中的一个：
  * cloth
  * chain
  * iron
  * gold
  * diamond

注意

* armor类型对texture域的解析有所不同。根据mc的盔甲贴图绘制规则，[贴图路径]_0.png是除了裤子的其他贴图，[贴图路径]_1.png是裤子的贴图。


添加贴图和语言文件的说明
---

语言文件建议在.minecraft/mods文件夹中加入相应lang。MC会自动加载。(/assets/<modid>/lang/[lang].lang)

贴图文件建议在.minecraft/mods文件夹中加入相应贴图。(/assets/<modid>/textures/xxxxx.lang)


配置文件示例
---
```
[
{
	"name": "super_sword",
	"creativeTab": "misc",
	"type": "sword",
	"toolClass": "diamond",
	"texture": "mymod:233"
},
{
	"name": "stupid_pickaxe",
	"type": "pickaxe",
	"toolClass": "wood",
	"texture": "diamond_pickaxe"
},
{
	"name": "some_armor",
	"type": "armor",
	"armorClass": "chain",
	"texture": "xxx"
}
]
```