package com.example.myfishermanapplication.database

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myfishermanapplication.model.Catch
import com.example.myfishermanapplication.model.Fish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Fish::class, Catch::class], version = 32)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fishDao(): FishDao
    abstract fun catchDao(): CatchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getDatabase(context)
                                populateDatabase(database.fishDao())
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }



        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d("AppDatabase", "onCreate triggered")
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.fishDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(fishDao: FishDao) {
            Log.d("AppDatabase", "populateDatabase called")

            val predefinedFishes = listOf(
                Fish(name = "Boleń", length = "50", bait= "Woblery, obrotówki, gumy", protectiveDimension = "40", description = "Drapieżnik z rodziny karpiowatych, szybki i waleczny.", imageUri = "fish/bolen.jpg"),
                Fish(name = "Jesiotr", length = "150", bait= "Rosówki, martwa rybka", protectiveDimension = "70", description = "Duża ryba o wydłużonym ciele, rzadko spotykana w naturalnych wodach.", imageUri = "fish/jesiotr.jpg"),
                Fish(name = "Karaś srebrzysty", length = "35", bait= "Ciasto, kukurydza, czerwone robaki", protectiveDimension = "15", description = "Podobny do karasia złocistego, lecz ma srebrzystą barwę.", imageUri = "fish/karas_srebrzysty.jpg"),
                Fish(name = "Karaś złocisty", length = "30", bait= "Ciasto, kukurydza, czerwone robaki", protectiveDimension = "15", description = "Popularna ryba spokojnego żeru o złocistej łusce.", imageUri = "fish/kras_zlocisty.jpg"),
                Fish(name = "Karp", length = "90", bait= "Kulki proteinowe, kukurydza, pellet", protectiveDimension = "30", description = "Popularna ryba hodowlana, ceniona w wędkarstwie sportowym.", imageUri = "fish/karp.jpg"),
                Fish(name = "Kleń", length = "50", bait= "Chleb, woblery, czerwone robaki", protectiveDimension = "25", description = "Silna ryba z rodziny karpiowatych, spotykana w rzekach i jeziorach.", imageUri = "fish/klen.jpg"),
                Fish(name = "Leszcz", length = "60", bait= "Białe robaki, kukurydza, ciasto", protectiveDimension = "25", description = "Ryba spokojnego żeru, często łowiona przez wędkarzy spławikowych.", imageUri = "fish/leszcz.jpg"),
                Fish(name = "Lin", length = "50", bait= "Rosówki, kukurydza, ciasto", protectiveDimension = "25", description = "Charakterystyczna zielonkawo-złota ryba z małymi łuskami.", imageUri = "fish/lin.jpg"),
                Fish(name = "Okoń", length = "40", bait= "Twistery, obrotówki, rosówki", protectiveDimension = "15", description = "Drapieżnik o zielonkawym ubarwieniu i czerwonych płetwach.", imageUri = "fish/okon.jpg"),
                Fish(name = "Pstrąg potokowy", length = "55", bait= "Woblery, błystki, muchy", protectiveDimension = "30", description = "Szybka, drapieżna ryba zamieszkująca górskie potoki.", imageUri = "fish/pstrag_potokowy.jpg"),
                Fish(name = "Pstrąg tęczowy", length = "60", bait= "Woblery, błystki, muchy", protectiveDimension = "30", description = "Introdukowana ryba drapieżna, popularna w hodowlach i łowiskach specjalnych.", imageUri = "fish/pstrag_teczowy.jpg"),
                Fish(name = "Sandacz", length = "100", bait= "Gumy, woblery, martwa rybka", protectiveDimension = "45", description = "Drapieżnik z rodziny okoniowatych, ceniony przez wędkarzy.", imageUri = "fish/sandacz.jpg"),
                Fish(name = "Sielawa", length = "40", bait= "Ochotka, białe robaki", protectiveDimension = "25", description = "Ryba zimnowodna, smukła i srebrzysta, spotykana w czystych jeziorach.", imageUri = "fish/sielawa.jpg"),
                Fish(name = "Sum", length = "250", bait= "Martwa rybka, wątroba, rosówki", protectiveDimension = "70", description = "Największa drapieżna ryba słodkowodna w Polsce, osiągająca ogromne rozmiary.", imageUri = "fish/sum.jpg"),
                Fish(name = "Szczupak", length = "150", bait= "Woblery, gumy, martwa rybka", protectiveDimension = "45", description = "Najpopularniejszy drapieżnik w Polsce, znany z nagłych ataków.", imageUri = "fish/szczupak.jpg"),
                Fish(name = "Troć wędrowna", length = "90", bait= "Woblery, błystki, muchy", protectiveDimension = "50", description = "Wędrowna ryba łososiowata, spotykana w rzekach i Bałtyku.", imageUri = "fish/troc_wedrowna.jpg"),
                Fish(name = "Węgorz", length = "150", bait= "Rosówki, martwa rybka, wątroba", protectiveDimension = "70", description = "Wężokształtna ryba nocna, ceniona za smaczne mięso.", imageUri = "fish/wegorz.jpg"),
                Fish(name = "Wzdręga", length = "40", bait= "Ciasto, kukurydza, białe robaki", protectiveDimension = "15", description = "Ryba o czerwonych płetwach, przypominająca płoć.", imageUri = "fish/wzdrega.jpg"),
                Fish(name = "Zander", length = "100", bait= "Gumy, woblery, martwa rybka", protectiveDimension = "45", description = "Drapieżnik podobny do sandacza, często mylony z nim.", imageUri = "fish/zander.jpg"),
            )
            fishDao.insertAll(predefinedFishes)
        }
    }
}





