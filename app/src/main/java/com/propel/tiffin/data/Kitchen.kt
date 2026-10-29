
package com.propel.tiffin.data

data class Kitchen(
    val id: String,
    val name: String,
    val cuisine: String,
    val price: Int,
    val isVeg: Boolean,
    val rating: Double,
    val description: String,
    val deliveryTime: String,
    val tags: List<String>,
    val menu: List<MenuItem>
)

data class MenuItem(
    val day: String,
    val mealName: String,
    val description: String
)
