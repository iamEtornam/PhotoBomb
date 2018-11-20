package co.etornam.photobomb.ui

import `in`.mayanknagwanshi.imagepicker.imageCompression.ImageCompressionListener
import `in`.mayanknagwanshi.imagepicker.imagePicker.ImagePicker
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import co.etornam.photobomb.R
import com.tfb.fbtoast.FBToast
import kotlinx.android.synthetic.main.activity_profile_info.*


class ProfileInfoActivity : AppCompatActivity() {
	private lateinit var imagePicker: ImagePicker

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_profile_info)

		profileImage.setOnClickListener {
			imagePicker = ImagePicker()
			imagePicker.withActivity(this)
					.chooseFromCamera(true)
					.chooseFromGallery(true)
					.withCompression(true)
					.start()
		}

		saveBtn.setOnClickListener {
			animateButtonAndRevert(saveBtn,
					ContextCompat.getColor(this@ProfileInfoActivity, R.color.green),
					getBitmap(R.drawable.ic_check))
		}
	}

	private fun animateButtonAndRevert(circularProgressButton: CircularProgressButton,
	                                   fillColor: Int,
	                                   bitmap: Bitmap) {
		val doneAnimationRunnable = {
			circularProgressButton.doneLoadingAnimation(
					fillColor,
					bitmap)
		}
		circularProgressButton.startAnimation()







		with(Handler()) {
			postDelayed(doneAnimationRunnable, 3000)
			postDelayed({ circularProgressButton.revertAnimation() }, 4000)
		}
	}


	override fun onDestroy() {
		super.onDestroy()
		saveBtn.dispose()
	}

	private fun getBitmap(drawableRes: Int): Bitmap {
		val drawable = resources.getDrawable(drawableRes)
		val canvas = Canvas()
		val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
		canvas.setBitmap(bitmap)
		drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
		drawable.draw(canvas)

		return bitmap
	}


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == ImagePicker.SELECT_IMAGE && resultCode == Activity.RESULT_OK) {

			//Add compression listener if withCompression is set to true
			imagePicker.addOnCompressListener(object : ImageCompressionListener {
				override fun onStart() {
					FBToast.infoToast(applicationContext, "Compressing started!", FBToast.LENGTH_SHORT)
				}

				override fun onCompressed(filePath: String) {
					//filePath of the compressed image
					//convert to bitmap easily
					val selectedImage = BitmapFactory.decodeFile(filePath)
					profileImage.setImageBitmap(selectedImage)
					FBToast.successToast(applicationContext, "Compressing Done!", FBToast.LENGTH_SHORT)
				}
			})
		}
		//call the method 'getImageFilePath(Intent data)' even if compression is set to false
		val filePath = imagePicker.getImageFilePath(data)
		if (filePath != null) {
			//filePath will return null if compression is set to true
			val selectedImage = BitmapFactory.decodeFile(filePath)
			profileImage.setImageBitmap(selectedImage)
		}
		super.onActivityResult(requestCode, resultCode, data)
	}
}
