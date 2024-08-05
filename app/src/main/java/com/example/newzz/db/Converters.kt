package com.example.newzz.db

import androidx.room.TypeConverter
import com.example.newzz.model.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return Gson().toJson(source)
    }

    @TypeConverter
    fun toSource(sourceString: String): Source {
        val sourceType = object : TypeToken<Source>() {}.type
        return Gson().fromJson(sourceString, sourceType)
    }
}