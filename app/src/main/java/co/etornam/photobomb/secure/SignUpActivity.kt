package co.etornam.photobomb.secure

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.etornam.photobomb.R
import co.etornam.photobomb.ui.MainActivity
import co.etornam.photobomb.util.NetworkUtil.isNetworkAvailable
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.tfb.fbtoast.FBToast
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_sign_up)

		signunBtn.setOnClickListener {
			if (isNetworkAvailable(this)) {
				createSignInIntent()
			} else {
				FBToast.errorToast(applicationContext, "No Internet Connection!", FBToast.LENGTH_SHORT)
			}
		}
	}

	private fun createSignInIntent() {
		// Choose authentication providers
		val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

// Create and launch sign-in intent
		startActivityForResult(
				AuthUI.getInstance()
						.createSignInIntentBuilder()
						.setAvailableProviders(providers)
						.setIsSmartLockEnabled(true)
						.setLogo(R.drawable.ic_add_a_photo_black)      // Set logo drawable
						.setTheme(R.style.AppTheme)      // Set theme
						.build(),
				RC_SIGN_IN)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (requestCode == RC_SIGN_IN) {
			val response = IdpResponse.fromResultIntent(data)
			if (resultCode == Activity.RESULT_OK) {
				// Successfully signed in
				val user = FirebaseAuth.getInstance().currentUser
				FBToast.successToast(applicationContext, "Welcome " + user!!.displayName, FBToast.LENGTH_SHORT)
				startActivity(Intent(applicationContext, MainActivity::class.java))
				finish()
			} else {
				// Sign in failed. If response is null the user canceled the
				// sign-in flow using the back button. Otherwise check
				// response.getError().getErrorCode() and handle the error.
				// ...
				if (response == null) {
					// User pressed back button
					FBToast.errorToast(applicationContext, "SignIn process Cancelled!", FBToast.LENGTH_SHORT)
					return
				}

				if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
					FBToast.errorToast(applicationContext, "No Internet Connection!", FBToast.LENGTH_SHORT)
					return
				}
			}
		}
	}

	companion object {
		private const val RC_SIGN_IN = 123
	}

	override fun onStart() {
		super.onStart()
		val auth: FirebaseAuth = FirebaseAuth.getInstance()
		if (auth.currentUser != null) {
			startActivity(Intent(applicationContext, MainActivity::class.java))
		}
	}
}
