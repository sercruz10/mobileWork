package com.nodomain.freeyourgadget.gadgetbridge.pm.adapters

import android.content.Context
import org.json.JSONException
import org.json.JSONObject


class FinanceRecipe(
        val title: String,
        val imageUrl: String) {


    companion object {

        fun getRecipesFromFile(filename: String, context: Context, contexto: String): ArrayList<FinanceRecipe> {
            val recipeList = ArrayList<FinanceRecipe>()

            try {
                // Load data
                val recipes = JSONObject(loadJsonFromAsset(filename, context)).getJSONObject("config").getJSONObject("finance").getJSONObject(contexto)

                val keys = recipes.keys()
                while (keys.hasNext()) {
                    val key = keys.next() as String
                    recipes.get(key)
                    recipeList.add(FinanceRecipe(key, recipes.get(key).toString() ))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return recipeList
        }


        private fun loadJsonFromAsset(filename: String, context: Context): String? {
            var json: String?

            try {
                val inputStream = context.assets.open(filename)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                json = String(buffer, Charsets.UTF_8)
            } catch (ex: java.io.IOException) {
                ex.printStackTrace()
                return null
            }

            return json
        }
    }
}