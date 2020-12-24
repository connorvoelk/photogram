package edu.uw.voelkc.photogram

import android.app.PendingIntent.getActivity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.post_item.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TAG = "GalleryFragment"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment : Fragment() {

    inner class PhotoHolder(view: View): RecyclerView.ViewHolder(view){
        val postCaption : TextView = view.findViewById<TextView>(R.id.post_caption)
        val likeCount : TextView = view.findViewById<TextView>(R.id.like_count)
        val postImage: ImageView = view.findViewById<ImageView>(R.id.post_image)
        val likeButton: ImageButton = view.findViewById<ImageButton>(R.id.like_button)
        val userName: TextView = view.findViewById<TextView>(R.id.txt_user)
    }

    private val TAG = "GalleryFragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewData = mutableListOf<String>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val rootView = inflater.inflate(R.layout.fragment_gallery, container, false)

        val fab = rootView.findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            if(Firebase.auth.currentUser != null){ //user is not logged in
                findNavController().navigate(R.id.upload_fragment)
            } else { //user is logged inv
                Toast.makeText(activity?.applicationContext, "Must be logged in to upload a post", Toast.LENGTH_LONG).show()
            }
        }
        val fabListener = Firebase.auth.addAuthStateListener {if(Firebase.auth.currentUser == null){ //user is not logged in
            fab.hide()
        } else { //user is logged in
            fab.show()
        }}

        if((activity as MainActivity).colorMode == 1 ){
            val v: View = rootView.findViewById(R.id.fab)
            v.backgroundTintList = ColorStateList.valueOf(getResources().getColor(R.color.red_700))
        } else if ((activity as MainActivity).colorMode == 0 ){
            val v: View = rootView.findViewById(R.id.fab)
            v.backgroundTintList = ColorStateList.valueOf(getResources().getColor(R.color.purple_500))
        } else if ((activity as MainActivity).colorMode == 2 ){
            val v: View = rootView.findViewById(R.id.fab)
            v.backgroundTintList = ColorStateList.valueOf(getResources().getColor(R.color.teal_700))
        }

        val options = FirebaseRecyclerOptions.Builder<PhotoData>()
                //TODO assume setQuery Works for now, revise later
                .setQuery(Firebase.database.getReference("photos/"), PhotoData::class.java)
                .setLifecycleOwner(this)
                .build()

        val adapter = object : FirebaseRecyclerAdapter<PhotoData, PhotoHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
                return PhotoHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.post_item, parent, false))
            }

            protected override fun onBindViewHolder(holder: PhotoHolder, position: Int, model: PhotoData) {
                holder.postCaption.text = model.title

                holder.userName.text = model.userName

                val imageRef = getRef(position)
                Log.v(TAG, imageRef.toString())

                val old = model.likes
                val userUID: String? = Firebase.auth.currentUser?.uid
                //sets the color on launch
                if(old.contains(userUID)) { //if they already liked it!; They are now unliking
                    holder.likeButton.imageTintList = ColorStateList.valueOf(context!!.getColor(R.color.purple_500))
                } else { //if they did not already like it; They are now liking
                    holder.likeButton.imageTintList = ColorStateList.valueOf(context!!.getColor(R.color.black))
                }

                holder.likeButton.setOnClickListener{
                    if(userUID != null) {
                        val whereNewGoes = imageRef.child("likes")
                        if (old.contains(userUID)) { //if they already liked it!; They are now unliking
                            old.remove(userUID)
                            whereNewGoes.setValue(old)
                            holder.likeButton.imageTintList = ColorStateList.valueOf(context!!.getColor(R.color.purple_500))
                        } else { //if they did not already like it; They are now liking
                            Firebase.auth.currentUser?.uid?.let { it1 -> old.put(it1, true) }
                            whereNewGoes.setValue(old)
                            holder.likeButton.imageTintList = ColorStateList.valueOf(context!!.getColor(R.color.black))
                        }
                    } else {
                        Toast.makeText(activity?.applicationContext, "Must be logged in to interact with posts", Toast.LENGTH_LONG).show()
                    }
                }

                holder.likeCount.text = model.likes.size.toString()

                Glide.with(gallery_fragment)
                    .load(model.imgURL)
                    .fallback(ColorDrawable(Color.GRAY))
                    .into(holder.postImage)
            }

            override fun onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                // mEmptyListMessage.setVisibility(if (itemCount == 0) View.VISIBLE else View.GONE)
            }
        }
        val recycler = rootView.findViewById<RecyclerView>(R.id.recycler_view)
        recycler.layoutManager = LinearLayoutManager(this.context)
        recycler.adapter = adapter

        return rootView
    }

}




/*
class MainAdapter(private val theData: List<String>): RecyclerView.Adapter<MainAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val caption: TextView = view.findViewById<TextView>(R.id.post_caption)
        val likes: TextView = view.findViewById<TextView>(R.id.like_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflated = LayoutInflater.from(parent.context).inflate(
            R.layout.post_item,
            parent,
            false
        )
        return ViewHolder(inflated)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val theItem = theData[position]

    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }





}
*/