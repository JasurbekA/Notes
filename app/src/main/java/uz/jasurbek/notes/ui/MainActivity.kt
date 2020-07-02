package uz.jasurbek.notes.ui

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import uz.jasurbek.notes.R

class MainActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}