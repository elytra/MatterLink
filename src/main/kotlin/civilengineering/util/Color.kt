package civilengineering.util

fun String.color(color: Color):String {
    return color.code+this
}

enum class Color(val index:Int,val code:String) {
    BLACK(0,"§0"),
    DARK_BLUE(1,"§1"),
    DARK_GREEN(2,"§2"),
    DARK_AQUA(3,"§3"),
    DARK_RED(4,"§4"),
    DARK_PURPLE(5,"§5"),
    GOLD(6,"§6"),
    GRAY(7,"§7"),
    DARK_GRAY(8,"§8"),
    BLUE(9,"§9"),
    GREEN(10,"§a"),
    AQUA(11,"§b"),
    RED(12,"§c"),
    LIGHT_PURPLE(13,"§d"),
    YELLOW(14,"§e"),
    WHITE(15,"§f")
}