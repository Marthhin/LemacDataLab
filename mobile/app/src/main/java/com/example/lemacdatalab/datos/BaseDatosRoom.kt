package com.example.lemacdatalab.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import com.example.lemacdatalab.model.RegistroActividad
import com.example.lemacdatalab.model.Usuario
import com.example.lemacdatalab.model.VersionRegistro

@Database(
    entities = [RegistroActividad::class, Usuario::class, VersionRegistro::class],
    version = 6,
    exportSchema = true
)
abstract class BaseDatosRoom : RoomDatabase() {

    abstract fun registroDao(): RegistroDao

    abstract fun usuarioDao(): UsuarioDao

    companion object {

        private const val NOMBRE_ARCHIVO = "lemac_registros.db"

        private fun crear(context: Context): BaseDatosRoom {
            CifradoBaseDatos.cargarBiblioteca()
            val frase = CifradoBaseDatos.obtenerFrase(context)
            CifradoBaseDatos.cifrarSiEsTextoPlano(context, NOMBRE_ARCHIVO, frase)

            return Room.databaseBuilder(context, BaseDatosRoom::class.java, NOMBRE_ARCHIVO)
                .openHelperFactory(SupportOpenHelperFactory(frase.toByteArray(Charsets.UTF_8)))
                .addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5, MIGRACION_5_6)
                .build()
        }

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

        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                listOf(
                    "emocion",
                    "habilidad",
                    "estrategia",
                    "desencadenante",
                    "accion_protectora",
                    "resultado"
                ).forEach { columna ->
                    db.execSQL("ALTER TABLE registros ADD COLUMN $columna TEXT")
                }
            }
        }

        private val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE registros ADD COLUMN elevacion INTEGER")
                db.execSQL("ALTER TABLE registros ADD COLUMN horas_sueno REAL")
                listOf("alto_riesgo", "editado", "eliminado", "fecha_modificacion").forEach { columna ->
                    db.execSQL("ALTER TABLE registros ADD COLUMN $columna INTEGER NOT NULL DEFAULT 0")
                }

                db.execSQL(
                    """
                    UPDATE registros
                    SET intensidad = CAST(ROUND((MIN(MAX(intensidad, 1), 10) - 1) * 4.0 / 9) AS INTEGER)
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS registro_versiones (
                        id_version INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        id_registro INTEGER NOT NULL,
                        fecha_guardado INTEGER NOT NULL,
                        tipo_actividad TEXT NOT NULL,
                        intensidad INTEGER NOT NULL,
                        elevacion INTEGER,
                        contexto TEXT NOT NULL,
                        nota TEXT,
                        detalles TEXT
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRACION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN nombre TEXT")
                db.execSQL("ALTER TABLE usuarios ADD COLUMN objetivo TEXT")
                db.execSQL("ALTER TABLE registros ADD COLUMN uuid TEXT")
                db.execSQL("UPDATE registros SET uuid = lower(hex(randomblob(16))) WHERE uuid IS NULL")
            }
        }

        fun obtenerInstancia(context: Context): BaseDatosRoom {
            return instancia ?: synchronized(this) {
                instancia ?: crear(context.applicationContext).also { instancia = it }
            }
        }
    }
}