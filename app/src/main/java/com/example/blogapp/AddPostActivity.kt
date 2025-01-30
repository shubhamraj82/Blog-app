




package com.example.blogapp

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.util.HashMap

class AddPostActivity : AppCompatActivity() {
    private lateinit var title_blog: EditText
    private lateinit var description_blog: EditText
    private lateinit var upload: Button
    private lateinit var blog_image: ImageView

    private var imageUri: Uri? = null
    private val GALLERY_IMAGE_CODE=100
    private val CAMERA_IMAGE_CODE=200
    private lateinit var pd: ProgressDialog
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar
        actionBar?.title = "Add Post"
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)


        permission()

        title_blog = findViewById(R.id.title_blog)
        description_blog = findViewById(R.id.description_blog)
        upload = findViewById(R.id.upload)
        blog_image = findViewById(R.id.post_image_blog)

        pd= ProgressDialog(this)
        auth= FirebaseAuth.getInstance()

        blog_image.setOnClickListener {
            imagePickDialog()
        }

        upload.setOnClickListener {
            val title=title_blog.text.toString()
            val description=description_blog.text.toString()
            if (title.isEmpty()){
                title_blog.error="Title Required"
            }else if (description.isEmpty()){
                description_blog.error="Description Required"
            }else{
                uploadData(title, description)
            }
        }
    }

    private fun uploadData(title: String, description: String) {
        pd.setMessage("Publishing post")
        pd.show()
        val timeStamp = System.currentTimeMillis().toString()
        val filepath = "Posts/post_$timeStamp"

        if (blog_image.drawable != null) {
            val bitmap = (blog_image.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()

            val reference = FirebaseStorage.getInstance().getReference(filepath)
            reference.putBytes(data)
                .addOnSuccessListener { taskSnapshot ->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val downloadUri = uriTask.result.toString()

                    if (uriTask.isSuccessful) {
                        val user = auth.currentUser
                        val hashMap = HashMap<String, Any>()
                        hashMap.put("uid", user!!.uid)
                        hashMap.put("uEmail", user.email!!)
                        hashMap.put("pId", timeStamp)
                        hashMap.put("pTitle", title)
                        hashMap.put("pImage", downloadUri)
                        hashMap.put("pDescription", description)
                        hashMap.put("pTime", timeStamp)

                        val ref = FirebaseDatabase.getInstance().getReference("Posts")
                        ref.child(timeStamp).setValue(hashMap)
                            .addOnSuccessListener {
                                pd.dismiss()
                                Toast.makeText(this@AddPostActivity, "Post Published", Toast.LENGTH_SHORT).show()
                                title_blog.setText("")
                                description_blog.setText("")
                                blog_image.setImageURI(null)
                                imageUri = null
                                startActivity(Intent(this@AddPostActivity, HomeActivity::class.java))
                            }
                            .addOnFailureListener { e ->
                                pd.dismiss()
                                Toast.makeText(this@AddPostActivity, e.message, Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    pd.dismiss()
                    Toast.makeText(this@AddPostActivity, e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun imagePickDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Image From")
        builder.setItems(options){ dialog, which ->
            when(which){
                0 -> {
                    cameraPick()
                }
                1 -> {
                    galleryPick()
                }
            }
        }
        builder.create().show()
    }

    private fun galleryPick() {
        val intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent, GALLERY_IMAGE_CODE)
    }

    private fun cameraPick() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp pick")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        imageUri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CAMERA_IMAGE_CODE)
    }



    private fun permission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {}
                override fun onPermissionDenied(response: PermissionDeniedResponse) {}
                override fun onPermissionRationaleShouldBeShown(request: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {}
                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {}
            }).check()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GALLERY_IMAGE_CODE -> {
                    imageUri = data?.data
                    blog_image.setImageURI(imageUri)
                }
                CAMERA_IMAGE_CODE -> blog_image.setImageURI(imageUri)
            }
        }
    }
}