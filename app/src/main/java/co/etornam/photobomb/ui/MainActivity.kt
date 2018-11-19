package co.etornam.photobomb.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import co.etornam.photobomb.R
import co.etornam.photobomb.fragment.BottomNavigationFragment
import co.etornam.photobomb.secure.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_activity.*


class MainActivity : AppCompatActivity() {

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(bottom_appbar)
		toolbarTitle.text = resources.getString(R.string.app_name)
		toolbar.elevation = 1.0f

		fab_upload.setOnClickListener {
			startActivity(Intent(applicationContext, UploadImageActivity::class.java))

		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_bottom_item, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when {
			item?.itemId == R.id.menu_account -> {
				startActivity(Intent(applicationContext, ProfileInfoActivity::class.java))
			}
			item?.itemId == android.R.id.home -> {
				val bottomNavDrawerFragment = BottomNavigationFragment()
				bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
			}
		}
		return true
	}

	override fun onStart() {
		super.onStart()
		val auth: FirebaseAuth = FirebaseAuth.getInstance()
		if (auth.currentUser == null) {
			startActivity(Intent(applicationContext, SignUpActivity::class.java))
		}
	}

}
