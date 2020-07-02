package uz.jasurbek.notes.di.app

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import uz.jasurbek.notes.date.Constants.DB_NAME
import uz.jasurbek.notes.date.local.NoteDatabase
import javax.inject.Singleton

@Module
class AppModule {




    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): NoteDatabase =
        Room.databaseBuilder(application, NoteDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

}