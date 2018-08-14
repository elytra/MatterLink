package matterlink

import blue.endless.jankson.JsonObject
import blue.endless.jankson.impl.SyntaxError
import kotlin.math.sqrt

fun JsonObject.parseDimensions(): List<Int> = getOrPutList("dimensions", emptyList(), "list of dimension ids")
fun JsonObject.parseAllDimensions(): Boolean = getOrDefault("allDimensions", false, "ignores dimension list")

sealed class Area {
    abstract val type: String
    abstract val dimensions: List<Int>
    abstract val allDimensions: Boolean

    abstract fun testInBounds(x: Int, y: Int, z: Int): Boolean

    fun testForDim(dimension: Int?): Boolean {
        if (allDimensions) return true
        if(dimension == null) return false
        return dimensions.contains(dimension)
    }

    companion object {
        fun parse(jsonObj: JsonObject): Area {
            val type: String = jsonObj.getOrDefault("type", "INFINITE", "Area type identifier")
            return when (type.toUpperCase()) {
                "INFINITE" -> Infinite.parse(jsonObj)
                "RADIUS" -> Radius.parse(jsonObj)
                "SPHERE" -> Sphere.parse(jsonObj)
                "BOX" -> Box.parse(jsonObj)
                "SQUARE" -> Square.parse(jsonObj)
                else -> throw SyntaxError("no Area type '$type' found")
            }
        }
    }

    data class Infinite(
            override val dimensions: List<Int> = listOf(),
            override val allDimensions: Boolean = false
    ) : Area() {
        override val type = "INFINITE"

        override fun testInBounds(x: Int, y: Int, z: Int): Boolean {
            return true
        }

        companion object {
            fun parse(jsonObj: JsonObject): Area {
                return Infinite(
                        dimensions = jsonObj.parseDimensions(),
                        allDimensions = jsonObj.parseAllDimensions()
                )
            }
        }

    }

    data class Radius(
            override val dimensions: List<Int> = listOf(),
            override val allDimensions: Boolean = false,
            val x: Int,
            val z: Int,
            val radius: Int?
    ) : Area() {
        override val type = "RADIUS"

        override fun testInBounds(x: Int, y: Int, z: Int): Boolean {
            if (radius == null) return true
            return sqrt(((this.x - x) * (this.x - x)) + ((this.z - z) * (this.z - z)).toFloat()) < this.radius
        }

        companion object {
            fun parse(jsonObj: JsonObject): Area {

                return Radius(
                        dimensions = jsonObj.parseDimensions(),
                        allDimensions = jsonObj.parseAllDimensions(),
                        x = jsonObj.getOrDefault("x", 0),
                        z = jsonObj.getOrDefault("z", 0),
                        radius = jsonObj.getReified("radius")
                )
            }
        }
    }

    class Sphere (
            override val dimensions: List<Int> = listOf(),
            override val allDimensions: Boolean = false,
            val x: Int,
            val y: Int,
            val z: Int,
            val radius: Int? = null
    ): Area() {
        override val type = "SPHERE"

        override fun testInBounds(x: Int, y: Int, z: Int): Boolean {
            if (radius == null) return true
            return sqrt(((this.x - x) * (this.x - x)) +((this.y - y) * (this.y - y)) + ((this.z - z) * (this.z - z)).toFloat()) < this.radius
        }

        companion object {
            fun parse(jsonObj: JsonObject): Area {

                return Sphere(
                        dimensions = jsonObj.parseDimensions(),
                        allDimensions = jsonObj.parseAllDimensions(),
                        x = jsonObj.getOrDefault("x", 0),
                        y = jsonObj.getOrDefault("y", 0),
                        z = jsonObj.getOrDefault("z", 0),
                        radius = jsonObj.getReified("radius")
                )
            }
        }
    }

    class Box (
            override val dimensions: List<Int> = listOf(),
            override val allDimensions: Boolean = false,
            val x1: Int,
            val x2: Int,
            val y1: Int,
            val y2: Int,
            val z1: Int,
            val z2: Int
    ): Area() {
        override val type = "BOX"

        override fun testInBounds(x: Int, y: Int, z: Int): Boolean {
            return x in x1..x2 && y in y1..y2 && z in z1..z2
        }

        companion object {
            fun parse(jsonObj: JsonObject): Area {

                return Box(
                        dimensions = jsonObj.parseDimensions(),
                        allDimensions = jsonObj.parseAllDimensions(),
                        x1 = jsonObj.getOrDefault("x1", 0),
                        x2 = jsonObj.getOrDefault("x2", 0),
                        y1 = jsonObj.getOrDefault("y1", 0),
                        y2 = jsonObj.getOrDefault("y2", 0),
                        z1 = jsonObj.getOrDefault("z1", 0),
                        z2 = jsonObj.getOrDefault("z2", 0)
                )
            }
        }
    }

    class Square (
            override val dimensions: List<Int> = listOf(),
            override val allDimensions: Boolean = false,
            val x1: Int,
            val x2: Int,
            val z1: Int,
            val z2: Int
    ): Area() {
        override val type = "SQUARE"

        override fun testInBounds(x: Int, y: Int, z: Int): Boolean {
            return x in x1..x2 && z in z1..z2
        }
        companion object {
            fun parse(jsonObj: JsonObject): Area {

                return Square(
                        dimensions = jsonObj.parseDimensions(),
                        allDimensions = jsonObj.parseAllDimensions(),
                        x1 = jsonObj.getOrDefault("x1", 0),
                        x2 = jsonObj.getOrDefault("x2", 0),
                        z1 = jsonObj.getOrDefault("z1", 0),
                        z2 = jsonObj.getOrDefault("z2", 0)
                )
            }
        }
    }
//
//
//    class FakePlayer (
//            val x: Int,
//            val y: Int,
//            val z: Int,
//            val name: String
//    ): Area()
}