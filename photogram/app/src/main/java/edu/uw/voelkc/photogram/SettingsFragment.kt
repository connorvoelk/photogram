package edu.uw.voelkc.photogram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var TAG: String? = "SettingsFragment"
    private var param2: String? = null
    private val SELECTED_RADIO = "selected_radio"
    private var selectedRadio = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            TAG = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        /*
        if(savedInstanceState != null){
            selectedRadio = savedInstanceState.getInt(SELECTED_RADIO)

            if (selectedRadio == R.id.red_radio) {
                settings_fragment.findViewById<FloatingActionButton>(R.id.fab).setBackgroundTintList(
                    ColorStateList.valueOf(getResources().getColor(R.color.teal_700))
                );
            } else if (selectedRadio == R.id.cyan_radio) {

            } else if (selectedRadio == R.id.purple_radio) {

            }
        }
        */


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        /*
        rootView.findViewById<RadioButton>(R.id.red_radio).setOnClickListener(this)
        rootView.findViewById(R.id.cyan_radio).setOnClickListener(this)
        rootView.findViewById(R.id.purple_radio).setOnClickListener(this)
        */


        val radioGroup = rootView.findViewById<RadioGroup>(R.id.radios)

        radioGroup.setOnCheckedChangeListener { group, checkedId -> // checkedId is the RadioButton selected
            when (checkedId) {
                R.id.red_radio -> {
                    // selected red_radio
                    // val theFab = gallery_fragment.findViewById<FloatingActionButton>(R.id.fab)


                    //val galleryView: View = requireView().findViewById<View>(R.id.gallery_fragment) as View

                    //galleryView.findViewById<FloatingActionButton>(R.id.fab).backgroundTintList = ColorStateList.valueOf(getResources().getColor(R.color.teal_700))
                    (activity as MainActivity?)?.changeRed()
                    findNavController().navigate(R.id.gallery_fragment)
                    // (activity as MainActivity?)!!.changeRed()
                    /*
                    theFab.setBackgroundTintList(
                            ColorStateList.valueOf(getResources().getColor(R.color.teal_700))
                    );
                    */
                    // Log.v(TAG, theFab.toString())
                }
                R.id.cyan_radio -> {
                    (activity as MainActivity?)?.changeCyan()
                    findNavController().navigate(R.id.gallery_fragment)
                }
                R.id.purple_radio -> {
                    (activity as MainActivity?)?.changePurple()
                    findNavController().navigate(R.id.gallery_fragment)
                }
            }
        }




        return rootView
    }

}