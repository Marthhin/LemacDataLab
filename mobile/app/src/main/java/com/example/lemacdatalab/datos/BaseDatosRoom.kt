package com.example.lemacdatalab.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lemacdatalab.model.RegistroActividad

@Database(
    entities = [RegistroActividad::class],
    version = 2,
    exportSchema = true
)
abstract class BaseDatosRoom : RoomDatabase() {

    abstract fun registroDao(): RegistroDao

    companion object {

        @Volatile
        private var instancia: BaseDatosRoom? = null

        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE registros_nueva (
                        id_registro INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        id_usuario INTEGER NOT NULL,
                        id_ambito INTEGER NOT NULL,
                        fecha_hora TEXT NOT NULL,
                        tipo_actividad TEXT NOT NULL,
                        intensidad INTEGER NOT NULL,
                        contexto TEXT NOT NULL,
                        nota TEXT,
                        estado TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO registros_nueva (
                        id_registro,
                        id_usuario,
                        id_ambito,
                        fecha_hora,
                        tipo_actividad,
                        intensidad,
                        contexto,
                        nota,
                        estado
                    )
                    SELECT
                        id_registro,
                        id_usuario,
                        id_ambito,
                        fecha_hora,
                        tipo_actividad,
                        intensidad,
                        contexto,
                        nota,
                        estado
                    FROM registros
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE registros")
                db.execSQL("ALTER TABLE registros_nueva RENAME TO registros")
            }
        }

        fun obtenerInstancia(context: Context): BaseDatosRoom {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosRoom::class.java,
                    "lemac_registros.db"
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()
                    .also { instancia = it }
            }
        }
    }
}