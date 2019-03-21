package com.mvi.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mvi.sample.news.NewsFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content, NewsFragment())
                .commit()
        }
    }
}
