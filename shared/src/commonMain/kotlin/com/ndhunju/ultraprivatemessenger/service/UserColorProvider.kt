package com.ndhunju.ultraprivatemessenger.service

object UserColorProvider {

    private const val RED      = "#ff6b6b"
    private const val GREEN    = "#57cc99"
    private const val BLUE     = "#5fa8d3"
    private const val PINK     = "#f15bb5"
    private const val YELLOW   = "#ffd000"
    private const val BLUE_SKY = "#00bbf9"

    private val userColorRefs = arrayOf(
        RED,
        GREEN,
        BLUE,
        PINK,
        YELLOW,
        BLUE_SKY,
    )

    /**
     * Provides a color for a given [userId] from a set of color.
     * For a given [userId] the returned color will always be same
     */
    fun provide(userId: Int): String {
        return userColorRefs[userId.mod(userColorRefs.size)]
    }
}