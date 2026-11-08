package com.example.lemacdatalab.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.Usuario

@Database(
    entities = [RegistroActividad::class, Usuario::class],
    version = 3,
    exportSchema = true
)
abstract class BaseDatosRoom : RoomDatabase() {

    abstract fun registroDao(): RegistroDao

    abstract fun usuarioDao(): UsuarioDao

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

        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        alias TEXT NOT NULL,
                        contrasena_hash TEXT NOT NULL,
                        sal TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_alias ON usuarios (alias)"
                )
            }
        }

        fun obtenerInstancia(context: Context): BaseDatosRoom {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosRoom::class.java,
                    "lemac_registros.db"
                )
                    .addMigrations(MIGRACION_1_2, MIGRACION_2_3)
                    .build()
                    .also { instancia = it }
            }
        }
    }
}