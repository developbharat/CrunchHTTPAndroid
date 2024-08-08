package com.developbharat.crunchhttp.domain.modules.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class UnixTimeStampDeserializer : JsonDeserializer<LocalDateTime?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime? {
        return if (json !== null) {
            Instant.ofEpochMilli(json.asLong).atZone(ZoneId.systemDefault()).toLocalDateTime()
        } else {
            null
        }
    }
}