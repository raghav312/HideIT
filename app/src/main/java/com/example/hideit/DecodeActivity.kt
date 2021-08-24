package com.example.hideit

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback
import com.ayush.imagesteganographylibrary.Text.ImageSteganography
import com.ayush.imagesteganographylibrary.Text.TextDecoding
import com.example.hideit.databinding.ActivityDecodeBinding


class DecodeActivity : AppCompatActivity(),TextDecodingCallback {

    private lateinit var binding: ActivityDecodeBinding
    private lateinit var decoded_cover_img:Bitmap
    private lateinit var imageSteganography:ImageSteganography
    private lateinit var textDecoding: TextDecoding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDecodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSelectImageD.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery , 100)
        }

        binding.btnDecodeThis.setOnClickListener {
            getData()
        }
    }

    private fun getData() {
        val sec_key = binding.editTextTextPersonName3.text.toString()
        if(sec_key.isEmpty()){
            Toast.makeText(this, "Fill in Fields.", Toast.LENGTH_SHORT).show()
        }else{
            imageSteganography = ImageSteganography(sec_key,decoded_cover_img)
            textDecoding = TextDecoding(this,this)
            textDecoding.execute(imageSteganography)
        }
    }
    

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == 100){
            var imgURI = data?.data
            binding.ivDecodedImage.setImageURI(imgURI)
            decoded_cover_img =  MediaStore.Images.Media.getBitmap(this.contentResolver,imgURI)
        }
    }

    override fun onStartTextEncoding() {

    }

    override fun onCompleteTextEncoding(result: ImageSteganography?) {
        
        if(result!=null){
            
            if(!result.isDecoded){
                Toast.makeText(this, "No Message found!", Toast.LENGTH_SHORT).show()
            }else{
                
                if(!result.isSecretKeyWrong){
                    Toast.makeText(this, "Decoded.", Toast.LENGTH_SHORT).show()
                    binding.tvMessage.text = result.message.toString()
                }else{
                    Toast.makeText(this, "Wrong Secret key!", Toast.LENGTH_SHORT).show()
                }
            }
            
        }else{
            Toast.makeText(this, "Enter an image", Toast.LENGTH_SHORT).show()
        }
        
    }



}