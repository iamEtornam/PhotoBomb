package co.etornam.photobomb.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import co.etornam.photobomb.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_navigation_bottom.*

class BottomNavigationFragment : BottomSheetDialogFragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment

		return inflater.inflate(R.layout.fragment_navigation_bottom, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		navigation_view.setNavigationItemSelectedListener {
			menuItem ->
			when(menuItem.itemId){
				R.id.friends ->
					Toast.makeText(context, "friends", Toast.LENGTH_SHORT).show()
				R.id.favourites ->
					Toast.makeText(context, "favourites", Toast.LENGTH_SHORT).show()
				R.id.settings ->
					Toast.makeText(context, "settings", Toast.LENGTH_SHORT).show()
			}
			true
		}
	}

}
