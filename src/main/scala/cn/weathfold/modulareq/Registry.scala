package cn.weathfold.modulareq

import net.minecraft.item.Item

object Registry {

  /**
    * Task status of item construction.
    */
  class Status {
    private var exec = Set[String]()

    /**
      * Mark an action as executed.
      */
    def markExec(action: String): Unit = {
      exec = exec + action
    }

    /**
      * Test whether the given action has been successfully executed
      */
    def isExecuted(action: String): Boolean = {
      exec contains action
    }
  }

  type ItemConstructor = Map[String, Any] => Item
  type TaskApply = (Item, Map[String, Any], Status) => Boolean

  /**
    * Define a item type with given constructor.
    */
  def defineType(name: String, x: ItemConstructor) = {
    if (typeActions.contains(name)) {
      error("Type with name " + name + " already exists")
    } else {
      val action = new TypeAction(name, x)
      typeActions = typeActions updated (name, action)
      allTasks.filter(_.typeFilter(action.name)).foreach(action.addTask)
    }
  }

  def addTaskOfType(itemType: String, name: String, fn: TaskApply, priority: Int = 0) = {
    addTask(name, fn, priority, _ == itemType)
  }

  /**
    * Adds a task to be performed on some item types.
    * @param name Name of the task
    * @param fn The action to apply
    * @param priority The priority of the task
    * @param typeFilter A predicate to filter the applied item types
    */
  def addTask(name: String, fn: TaskApply, priority: Int = 0, typeFilter: String => Boolean = x => true) = {
    val node = new Task(name, fn, priority, typeFilter)
    allTasks = node :: allTasks
    typeActions.values.filter(x => typeFilter(x.name)).foreach(_.addTask(node))
  }

  private var allTasks = List[Task]()

  private var typeActions : Map[String, TypeAction] = Map() withDefaultValue
    new TypeAction("undefined", m => {
      error("Undefined item type " + m("type"))
    })

  private class Task(val name: String, val fn : TaskApply,
                     val priority: Int, val typeFilter: String => Boolean)

  private class TypeAction(val name: String, val ctor: ItemConstructor) {

    private var tasks: List[Task] = Nil

    def addTask(node: Task): Unit = {
      tasks = node :: tasks sortBy (- _.priority)
    }

    def construct(data: Map[String, Any]): Item = {
      val item: Item = ctor(data)
      val status = new Status
      tasks.filter(x => if(!status.isExecuted(x.name)) x.fn(item, data, status) else false) foreach
        (x => status.markExec(x.name))

      item
    }

  }

  def error(message: Any, exc: Exception = null) = {
    throw new RuntimeException("[ModularEquipment] " + message, exc)
  }

  defineType("default", data => new Item)

}
