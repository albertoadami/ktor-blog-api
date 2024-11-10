package it.adami.services.blog.util

import org.jetbrains.exposed.sql.ColumnType
import org.postgresql.util.PGobject

class PGEnumColumnType<T : Enum<T>>(private val enumTypeName: String, private val klass: Class<T>) : ColumnType() {

    override fun sqlType() = enumTypeName

    override fun valueFromDB(value: Any): T = when (value) {
        is PGobject -> klass.enumConstants.first { it.name == value.value }
        is String -> klass.enumConstants.first { it.name == value }
        else -> error("Unexpected value of type Enum: $value of ${value::class.qualifiedName}")
    }

    override fun notNullValueToDB(value: Any): Any {
        val pgObject = PGobject().apply {
            type = enumTypeName
            this.value = (value as Enum<*>).name
        }
        return pgObject
    }

    override fun nonNullValueToString(value: Any): String = "'${(value as Enum<*>).name}'"
}
