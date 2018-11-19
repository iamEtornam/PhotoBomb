package co.etornam.photobomb.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import co.etornam.photobomb.R
import co.etornam.photobomb.secure.SignUpActivity
import co.etornam.photobomb.util.NetworkUtil.isNetworkAvailable
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tfb.fbtoast.FBToast
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
				R.id.logout ->
				if (isNetworkAvailable(this.context!!)){
					AuthUI.getInstance()
							.signOut(this.context!!)
							.addOnCompleteListener {
								if (it.isSuccessful){
									FBToast.successToast(context,"See you again soon!", FBToast.LENGTH_SHORT)
									startActivity(Intent(context, SignUpActivity::class.java))
								}else{
									FBToast.warningToast(context,"Something went wrong. Try Again!", FBToast.LENGTH_SHORT)
								}
							}
				}else{
					FBToast.warningToast(context,"No Internet Connection!", FBToast.LENGTH_SHORT)
				}
			}
			true
		}
	}

}
