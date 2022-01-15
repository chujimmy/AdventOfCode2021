package advent.day.day22.domain

import java.lang.Integer.max
import java.lang.Integer.min
import java.math.BigInteger
import kotlin.math.abs

class Cuboid(
    val xMin: Int,
    val xMax: Int,
    val yMin: Int,
    val yMax: Int,
    val zMin: Int,
    val zMax: Int,
    val state: Int = -1
) {

    fun getCubes(): List<Cube> {
        return (xMax downTo xMin).flatMap { x ->
            (yMax downTo yMin).flatMap { y ->
                (zMax downTo zMin).map { z ->
                    Cube(x, y, z)
                }
            }
        }
    }

    fun volume(): BigInteger {
        return BigInteger.valueOf(abs(xMax - xMin) + 1L) *
            BigInteger.valueOf(abs(yMax - yMin) + 1L) *
            BigInteger.valueOf(abs(zMax - zMin) + 1L)
    }

    companion object {
        fun fromString(cubeStr: String, status: String, initializationZoneOnly: Boolean): Cuboid {
            val cubeInfo = cubeStr.split(",")
            val xInfo = cubeInfo[0].substring(2).split("..").map { it.toInt() }
            val yInfo = cubeInfo[1].substring(2).split("..").map { it.toInt() }
            val zInfo = cubeInfo[2].substring(2).split("..").map { it.toInt() }

            if (initializationZoneOnly) {
                return Cuboid(
                    max(-50, xInfo[0]),
                    min(50, xInfo[1]),
                    max(-50, yInfo[0]),
                    min(50, yInfo[1]),
                    max(-50, zInfo[0]),
                    min(50, zInfo[1]),
                    if (status == "on") 1 else -1,
                )
            }

            return Cuboid(xInfo[0], xInfo[1], yInfo[0], yInfo[1], zInfo[0], zInfo[1], if (status == "on") 1 else -1)
        }
    }
}
