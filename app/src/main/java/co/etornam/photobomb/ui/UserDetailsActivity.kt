package co.etornam.photobomb.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.etornam.photobomb.R

class UserDetailsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_user_details)

		val userId = intent.getStringExtra("userId")
	}
}
