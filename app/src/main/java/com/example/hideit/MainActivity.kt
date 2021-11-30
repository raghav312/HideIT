package com.example.hideit

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback
import com.ayush.imagesteganographylibrary.Text.ImageSteganography
import com.ayush.imagesteganographylibrary.Text.TextEncoding
import com.example.hideit.databinding.ActivityMainBinding
import java.io.*

class MainActivity : AppCompatActivity() , TextEncodingCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageSteganography:ImageSteganography
    private lateinit var textEncoding:TextEncoding
    private lateinit var cover_img:Bitmap
    private lateinit var encoded_img:Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnEncode.setOnClickListener {
            getData()
        }
        binding.btnDecode.setOnClickListener {
            val intent = Intent(this,DecodeActivity::class.java)
            startActivity(intent)
        }
        binding.btnSelectImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery , 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == 100){
            var imgURI = data?.data
            binding.ivCoverImage.setImageURI(imgURI)
            cover_img =  MediaStore.Images.Media.getBitmap(this.contentResolver,imgURI)
        }
    }

    private fun getData() {
        val msg = binding.editTextTextPersonName.text.toString()
        val secret_key = binding.editTextTextPersonName2.text.toString()
        if(msg.isEmpty() || secret_key.isEmpty()){
            Toast.makeText(this, "Fill in fields.", Toast.LENGTH_SHORT).show()
        }else{

            // get Anaonymized msg from api and pass it into ImageStegnography object declaration below


            imageSteganography = ImageSteganography(msg,secret_key,cover_img)
            textEncoding = TextEncoding(this , this)
            textEncoding.execute(imageSteganography)
        }
    }

    override fun onStartTextEncoding() {

    }

    override fun onCompleteTextEncoding(result: ImageSteganography?) {
        if(result!=null && result.isEncoded){
            encoded_img = result.encoded_image
            Toast.makeText(this, "Encoded.", Toast.LENGTH_SHORT).show()
            saveToGallery(this, encoded_img,"hideIT")
        }
    }
    fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String) {
        val filename = "${System.currentTimeMillis()}.png"
        val write: (OutputStream) -> Boolean = {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/$albumName")
            }

            context.contentResolver.let {
                it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                    it.openOutputStream(uri)?.let(write)
                }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + albumName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, filename)
            write(FileOutputStream(image))
        }
        Toast.makeText(context, "File Saved.", Toast.LENGTH_SHORT).show()
    }
}