package edu.uw.voelkc.photogram

//import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.*
import java.time.LocalDateTime


class UploadFragment : Fragment() {

    private val TAG = "**UPLOAD**"
    private val PICK_PHOTO_CODE: Int = 1046

    private var currentPreviewUri: Uri? = null
    private var title = ""

    private val storage = Firebase.storage
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_upload, container, false)
        val imgPreview = rootView.findViewById<ImageView>(R.id.img_preview)
        val btnSubmit = rootView.findViewById<Button>(R.id.btn_submit)
        val txtInputTitle = rootView.findViewById<EditText>(R.id.txt_input_title)

        //Preview Image
        if(currentPreviewUri != null){
            imgPreview.setImageBitmap(loadFromUri(currentPreviewUri))
        } else {
            imgPreview.setImageResource(R.mipmap.ic_choose)
        }

        imgPreview.setOnClickListener { this.onPickPhoto(rootView) }

        //Text Title Input
        txtInputTitle.addTextChangedListener(object : TextWatcher {
            //idk how to get rid of these
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                title = txtInputTitle.text.toString()
            }
        })

        //Submit Button
        btnSubmit.setOnClickListener{this.submit()}

        return rootView
    }

    fun submit(){
        if(currentPreviewUri != null){
            //Photo exists
            if(title != null || title != ""){
                //Photo and Title exist
                //Push up to db
                Toast.makeText(activity?.applicationContext, "Post Submitting", Toast.LENGTH_SHORT).show()
                Log.v(TAG, "Storage start")
                val path: String = "images/${UUID.randomUUID()}.png"
                val fireImagesRef :StorageReference = storage.getReference(path)
                val uploadTask = fireImagesRef.putFile(currentPreviewUri!!)

                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Toast.makeText(activity?.applicationContext, "Uh oh, there was a problem", Toast.LENGTH_SHORT).show()

                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    Toast.makeText(activity?.applicationContext, "Post Submitted", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.gallery_fragment)
                    //Getting some data!
                    //var downloadUrl: Uri? = null
                    // Retrieve img url
                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        fireImagesRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //Get the URL
                            val downloadUri = task.result
                            Log.v(TAG, "$downloadUri")
                            //Construct the data
                            val fakeMap: MutableMap<String, Boolean> = mutableMapOf()
                            //Get the userUID
                            //val userUID = activity.getUserUID()
                            val user = Firebase.auth.currentUser
                            var name = user!!.email
                            name = if (name == null) {
                                ""
                            } else {
                                name?.substringBefore('@')
                            }
                            val data: PhotoData = PhotoData("$downloadUri", title, user!!.uid, name, fakeMap)
                            //Upload the data
                            //Create unique reference
                            val myRef = database.getReference("photos/$title${UUID.randomUUID()}")
                            myRef.setValue(data) // upload

                            Log.v(TAG, ServerValue.TIMESTAMP.toString())


                        } else {
                            // Handle failures
                            Toast.makeText(activity?.applicationContext, "Error: URL Not Received", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // Go back to Gallery ->
                }

            } else {
                Toast.makeText(activity?.applicationContext, "Error: No Title Inputted", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity?.applicationContext, "Error: No Image Uploaded", Toast.LENGTH_SHORT).show()
        }
    }

    fun onPickPhoto(view: View?) {
        // Create intent for picking a photo from the gallery
        val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE)
        }
    }

    fun loadFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source = activity?.getContentResolver()?.let {
                    ImageDecoder.createSource(
                            it,
                            photoUri!!
                    )
                }
                source?.let { ImageDecoder.decodeBitmap(it) }
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(activity?.getContentResolver(), photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    fun refreshFragment() {
        val ft = requireFragmentManager().beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) { //runs safely
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && requestCode == PICK_PHOTO_CODE) {
            val photoUri = data.data
            Log.v(TAG, photoUri.toString())

            //Set it to preview
            currentPreviewUri = photoUri
            refreshFragment()
        }
    }
}
