package com.queryholic.housekeepingbook.spec.clova.response

data class Vertices(val vertices: List<Vertex>) {
    fun getLeftUpperCorner(): Vertex = vertices[0]

    fun getWidth(): Double = vertices[1].x - vertices[0].x

    fun getHeight(): Double = vertices[3].y - vertices[0].y
}
