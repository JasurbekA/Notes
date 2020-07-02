package uz.jasurbek.notes

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import uz.jasurbek.notes.di.app.DaggerAppComponent

class BaseApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>  =
        DaggerAppComponent.builder().application(this).build()
}