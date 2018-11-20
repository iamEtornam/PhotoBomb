package co.etornam.photobomb.ui

import `in`.mayanknagwanshi.imagepicker.imageCompression.ImageCompressionListener
import `in`.mayanknagwanshi.imagepicker.imagePicker.ImagePicker
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import co.etornam.photobomb.R
import co.etornam.photobomb.secure.SignUpActivity
import co.etornam.photobomb.util.NetworkUtil.isNetworkAvailable
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.tfb.fbtoast.FBToast
import kotlinx.android.synthetic.main.activity_upload_image.*
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.*


class UploadImageActivity : AppCompatActivity() {
	private lateinit var imagePicker: ImagePicker
	lateinit var storage: FirebaseStorage
	lateinit var mAuth: FirebaseAuth
	private lateinit var userUid: String
	lateinit var mDatabase: FirebaseFirestore

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_upload_image)
		storage = FirebaseStorage.getInstance()
		mAuth = FirebaseAuth.getInstance()
		mDatabase = FirebaseFirestore.getInstance()

		imgView.setOnClickListener {
			imagePicker = ImagePicker()
			imagePicker.withActivity(this)
					.chooseFromCamera(true)
					.chooseFromGallery(true)
					.withCompression(true)
					.start()
		}

		uploadButton.setOnClickListener {

			if (isNetworkAvailable(this)) {
				animateButtonAndRevert(uploadButton,
						ContextCompat.getColor(this@UploadImageActivity, R.color.green),
						getBitmap(R.drawable.ic_check))
			} else {
				FBToast.warningToast(applicationContext, "No Internet Connection", FBToast.LENGTH_SHORT)
			}
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

	override fun onStart() {
		super.onStart()
		if (mAuth.currentUser == null) {
			startActivity(Intent(applicationContext, SignUpActivity::class.java))
			finish()
		} else {
			userUid = mAuth.currentUser!!.uid
		}
	}


	private fun animateButtonAndRevert(circularProgressButton: CircularProgressButton,
	                                   fillColor: Int,
	                                   bitmapi: Bitmap) {
		circularProgressButton.startAnimation()

		val array = ByteArray(10) // length is bounded by 7
		Random().nextBytes(array)
		val generatedString = String(array, Charset.forName("UTF-8"))


		val storageRef = storage.reference
		val imageRef: StorageReference? = storageRef.child("post_images").child(generatedString + ".jpg")

		imgView.isDrawingCacheEnabled = true
		imgView.buildDrawingCache()
		val bitmap = (imgView.drawable as BitmapDrawable).bitmap
		val baos = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
		val data = baos.toByteArray()

		val uploadTask = imageRef!!.putBytes(data)
		uploadTask.addOnCompleteListener {
			if (it.isSuccessful) {
				val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
					if (!task.isSuccessful) {
						task.exception?.let {
							throw it
						}
					}
					return@Continuation imageRef.downloadUrl
				}).addOnCompleteListener { task ->
					if (task.isSuccessful) {
						val date = Date()
						val downloadUri = task.result
						val mUserUpload = hashMapOf<String, Any>()
						mUserUpload.put("imageUrl", downloadUri.toString())
						mUserUpload.put("currentUserId", userUid)
						mUserUpload.put("timeStamp", date)
						mUserUpload.put("datePosted", FieldValue.serverTimestamp())
						mDatabase.collection("photoBlog").document().set(mUserUpload)
								.addOnCompleteListener {
									if (it.isSuccessful) {
										FBToast.successToast(applicationContext, "Photo Uploaded!", FBToast.LENGTH_SHORT)
										startActivity(Intent(applicationContext, MainActivity::class.java))
									} else {
										FBToast.errorToast(applicationContext, "Something went wrong!", FBToast.LENGTH_SHORT)
										Log.d("ProfileInfo", "onComplete: " + task.exception)
									}

									circularProgressButton.doneLoadingAnimation(
											fillColor,
											bitmapi)
									circularProgressButton.revertAnimation()

								}

					} else {
						FBToast.errorToast(applicationContext, "Something went wrong!", FBToast.LENGTH_SHORT)
						Log.d("ProfileInfo", "onComplete: " + task.exception)
					}
				}
			} else {
				Log.d("ProfileInfo", "onComplete: " + it.exception)
				FBToast.errorToast(applicationContext, "Something went wrong!", FBToast.LENGTH_SHORT)
			}
		}

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
}
