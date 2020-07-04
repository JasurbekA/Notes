package uz.jasurbek.notes.di.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import uz.jasurbek.notes.data.Constants.DB_NAME
import uz.jasurbek.notes.data.local.NoteDatabase
import javax.inject.Singleton

@Module
class AppModule {


    @Provides
    @Singleton
    fun provideContext(application: Application) : Context = application



    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): NoteDatabase =
        Room.databaseBuilder(application, NoteDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

}