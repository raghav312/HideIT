package com.example.hideit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback
import com.ayush.imagesteganographylibrary.Text.ImageSteganography
import com.example.hideit.databinding.ActivityDecodeBinding

class DecodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDecodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDecodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}