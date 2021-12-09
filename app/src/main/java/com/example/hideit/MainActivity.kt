package com.example.hideit

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback
import com.ayush.imagesteganographylibrary.Text.ImageSteganography
import com.ayush.imagesteganographylibrary.Text.TextEncoding
import com.example.hideit.databinding.ActivityMainBinding
import com.example.hideit.jsonclasses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() , TextEncodingCallback {

    private lateinit var webServices: WebServices
    private lateinit var webServices2: WebServices2
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageSteganography:ImageSteganography
    private lateinit var textEncoding:TextEncoding
    private lateinit var cover_img:Bitmap
    private lateinit var encoded_img:Bitmap
    private var anonymizedMsg:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webServices = WebServices.retrofit.create(WebServices::class.java)
        webServices2 = WebServices2.retrofit.create(WebServices2::class.java)
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
        binding.btnAnonymize.setOnClickListener {
            val msg = binding.editTextTextPersonName.text.toString()
            if(msg.isEmpty()){
                Toast.makeText(this, "Enter Message!", Toast.LENGTH_SHORT).show()
            }else{
                Log.i("anal","nahhhhh")
                getAnonText(msg)

            }
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
            // get Anonymized msg from api and pass it into ImageStegnography object declaration below
            if(anonymizedMsg.isNullOrEmpty()){
                anonymizedMsg = binding.editTextTextPersonName.text.toString()
            }
            imageSteganography = ImageSteganography(anonymizedMsg,secret_key,cover_img)
            textEncoding = TextEncoding(this , this)
            textEncoding.execute(imageSteganography)
        }
    }

    private fun getAnonText(msg: String) {

        var analyRes:AnalysizedResponse
        val dataToBeAnalyzed = AnalyserData(msg, "en" )

        var call:Call<AnalysizedResponse> = webServices.postAnalysized(dataToBeAnalyzed)

        call.enqueue(object :Callback<AnalysizedResponse>{
            override fun onResponse(
                call: Call<AnalysizedResponse>,
                response: Response<AnalysizedResponse>
            ) {
                if(response.isSuccessful){
                    //send text to be anonymized
                    analyRes = response.body()!!

                    Log.d("abc" , analyRes.size.toString())
                    performAnonimization(analyRes,msg)

                }else{
                    Log.d("abc" ,call.request().url().toString() )
                    Toast.makeText(this@MainActivity, response.code().toString() +" "+ response.message() , Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<AnalysizedResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Check Internet Connection!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performAnonimization(analyRes: AnalysizedResponse,msg:String) {

        var arr  = ArrayList<AnalyzerResult>()
        for(a:AnalysizedResponseItem in analyRes){
            var temp = AnalyzerResult(a.start ,a.end , a.score  ,a.entity_type)
            arr.add(temp)
        }
        Toast.makeText(this@MainActivity, "performing anonimization...", Toast.LENGTH_SHORT).show()


        var anonymizeText= AnonymizeText(msg , arr as List<AnalyzerResult>)
        var call:Call<AnonymizedResult> = webServices2.postAnonymizer(anonymizeText)
        call.enqueue(object :Callback<AnonymizedResult>{
            override fun onResponse(
                call: Call<AnonymizedResult>,
                response: Response<AnonymizedResult>
            ) {
                if(response.isSuccessful){
                    Toast.makeText(this@MainActivity, "Anonymization done!", Toast.LENGTH_SHORT).show()
                    binding.tvAnonText.text = response.body()!!.text
                    anonymizedMsg = response.body()!!.text

                }else{
                    Log.d("abc" ,call.request().url().toString() )
                    Toast.makeText(this@MainActivity, response.code().toString() +" "+ response.message() , Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AnonymizedResult>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Check Internet Connection!", Toast.LENGTH_SHORT).show()
            }

        })
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