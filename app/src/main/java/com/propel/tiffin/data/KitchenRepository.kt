
package com.propel.tiffin.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class KitchenRepository(
    private val context: Context
) {
    suspend fun getKitchens(): List<Kitchen> =
        withContext(Dispatchers.IO) {
            val json = context.assets
                .open("kitchens.json")
                .bufferedReader()
                .use { it.readText() }

            val array = JSONArray(json)

            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                val menuJson = item.getJSONArray("menu")
                val tagsJson = item.getJSONArray("tags")

                val menu = List(menuJson.length()) { menuIndex ->
                    val entry = menuJson.getJSONObject(menuIndex)

                    MenuItem(
                        day = entry.getString("day"),
                        mealName = entry.getString("mealName"),
                        description = entry.getString("description")
                    )
                }

                val tags = List(tagsJson.length()) { tagIndex ->
                    tagsJson.getString(tagIndex)
                }

                Kitchen(
                    id = item.getString("id"),
                    name = item.getString("name"),
                    cuisine = item.getString("cuisine"),
                    price = item.getInt("price"),
                    isVeg = item.getBoolean("isVeg"),
                    rating = item.getDouble("rating"),
                    description = item.getString("description"),
                    deliveryTime = item.getString("deliveryTime"),
                    tags = tags,
                    menu = menu
                )
            }
        }
}
