package co.etornam.photobomb.ui

import `in`.mayanknagwanshi.imagepicker.imageCompression.ImageCompressionListener
import `in`.mayanknagwanshi.imagepicker.imagePicker.ImagePicker
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.etornam.photobomb.R
import kotlinx.android.synthetic.main.activity_upload_image.*
import android.graphics.Bitmap
import com.tfb.fbtoast.FBToast
import android.graphics.drawable.BitmapDrawable
import android.view.View
import java.io.ByteArrayOutputStream


class UploadImageActivity : AppCompatActivity() {
	private lateinit var imagePicker: ImagePicker

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_upload_image)

		imgView.setOnClickListener {
			imagePicker = ImagePicker()
			imagePicker.withActivity(this)
					.chooseFromCamera(true)
					.chooseFromGallery(true)
					.withCompression(true)
					.start()
		}

		uploadButton.setOnClickListener {
				progressIndicator.visibility == View.VISIBLE



			/*imgView.isDrawingCacheEnabled = true
			imgView.buildDrawingCache()
			val bitmap = (imgView.drawable as BitmapDrawable).bitmap
			val baos = ByteArrayOutputStream()
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
			val data = baos.toByteArray()*/

		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == ImagePicker.SELECT_IMAGE && resultCode == Activity.RESULT_OK) {

			//Add compression listener if withCompression is set to true
			imagePicker.addOnCompressListener(object : ImageCompressionListener {
				override fun onStart() {
					FBToast.infoToast(applicationContext,"Compressing started!",FBToast.LENGTH_SHORT)
				}

				override fun onCompressed(filePath: String) {
					//filePath of the compressed image
					//convert to bitmap easily
					val selectedImage = BitmapFactory.decodeFile(filePath)
					imgView.setImageBitmap(selectedImage)
					FBToast.successToast(applicationContext,"Compressing Done!",FBToast.LENGTH_SHORT)
				}
			})
		}
		//call the method 'getImageFilePath(Intent data)' even if compression is set to false
		val filePath = imagePicker.getImageFilePath(data)
		if (filePath != null) {
			//filePath will return null if compression is set to true
			val selectedImage = BitmapFactory.decodeFile(filePath)
			imgView.setImageBitmap(selectedImage)
		}
		super.onActivityResult(requestCode, resultCode, data)
	}
}
