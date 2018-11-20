package co.etornam.photobomb.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.etornam.photobomb.R
import co.etornam.photobomb.fragment.BottomNavigationFragment
import co.etornam.photobomb.model.Post
import co.etornam.photobomb.secure.SignUpActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.kotlinpermissions.KotlinPermissions
import com.ldoublem.thumbUplib.ThumbUpView
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import com.tfb.fbtoast.FBToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_activity.*
import kotlinx.android.synthetic.main.single_row_layout.*


class MainActivity : AppCompatActivity() {
	private var adapter: FirestoreRecyclerAdapter<Post, PostViewHolder>? = null
	lateinit var mDatabase: FirebaseFirestore
	lateinit var mAuth: FirebaseAuth

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(bottom_appbar)

		KotlinPermissions.with(this) // where this is an FragmentActivity instance
				.permissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				.onAccepted { permissions ->
					//List of accepted permissions
				}
				.onDenied { permissions ->
					FBToast.warningToast(applicationContext, "This App will not function without the Denied Permission", FBToast.LENGTH_SHORT)
					finish()
				}
				.onForeverDenied { permissions ->

				}
				.ask()


		mAuth = FirebaseAuth.getInstance()
		mDatabase = FirebaseFirestore.getInstance()
		val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings
				.Builder()
				.setTimestampsInSnapshotsEnabled(true)
				.build()
		mDatabase.firestoreSettings = settings


		recyclerView.setHasFixedSize(true)
		val mLayoutManager = LinearLayoutManager(applicationContext)
		recyclerView.layoutManager = mLayoutManager
		mLayoutManager.orientation = RecyclerView.VERTICAL
		val itemDecoration = DividerItemDecoration(this,
				DividerItemDecoration.VERTICAL)
		recyclerView.addItemDecoration(itemDecoration)
		recyclerView.itemAnimator = DefaultItemAnimator()

		fab_upload.setOnClickListener {
			startActivity(Intent(applicationContext, UploadImageActivity::class.java))

		}

		val query = FirebaseFirestore.getInstance()
				.collection("photoBlog")
				.orderBy("datePosted")
				.limit(50)

		val options = FirestoreRecyclerOptions.Builder<Post>()
				.setQuery(query, Post::class.java)
				.build()

		adapter = object : FirestoreRecyclerAdapter<Post, PostViewHolder>(options) {
			override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
				var r: DocumentSnapshot = snapshots.getSnapshot(position)
				holder.setIsRecyclable(false)
				Picasso.get().load(model.imageUrl).into(holder.postImg)

				try {
					val userLikesRef = mDatabase.collection("photoBlog").document(r.id).collection("Likes").document(mAuth.currentUser!!.uid)
					val document = Tasks.await(userLikesRef.get())
					if (document.exists()) {
						likeBtn.Like()
					} else {
						likeBtn.UnLike()
					}

				} catch (e: Throwable) {

				}

				holder.postUsername.setOnClickListener {
					val intent = Intent(this@MainActivity, UserDetailsActivity::class.java)
					intent.putExtra("userId", model.currentUserId)
					startActivity(intent)
				}
				holder.postLike.setOnClickListener {
					Toast.makeText(this@MainActivity, "like", Toast.LENGTH_SHORT).show()

					mDatabase.collection("photoBlog").document(r.id).collection("Likes")
							.document(mAuth.currentUser!!.uid).get().addOnCompleteListener {
								if (!it.result!!.exists()) {
									val objectsMap = hashMapOf<String, Any>()
									objectsMap.put("timeStamp", FieldValue.serverTimestamp())
									mDatabase.collection("Posts").document(r.id).collection("Likes").document(mAuth.currentUser!!.uid).set(objectsMap)
								} else {
									mDatabase.collection("Posts").document(r.id).collection("Likes").document(mAuth.currentUser!!.uid).delete()

								}
							}
				}
			}

			override fun onCreateViewHolder(group: ViewGroup, i: Int): PostViewHolder {
				val view = LayoutInflater.from(group.context)
						.inflate(R.layout.single_row_layout, group, false)

				return PostViewHolder(view)
			}
		}
		recyclerView.adapter = adapter
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

		adapter!!.startListening()

	}

	override fun onStop() {
		super.onStop()
		adapter!!.stopListening()
		Log.d("ProfileInfo", "onComplete: ")
	}

	override fun onPause() {
		super.onPause()
		adapter!!.stopListening()
	}

	override fun onResume() {
		super.onResume()
		adapter!!.startListening()
	}


	//ViewHolder for our Firebase UI
	class PostViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
		internal var postUsername: TextView
		internal var senderImg: CircularImageView
		internal var postLike: ThumbUpView
		internal var postImg: ImageView

		init {
			postUsername = v.findViewById(R.id.usernameTxt)
			postLike = v.findViewById(R.id.likeBtn)
			senderImg = v.findViewById(R.id.profilePic)
			postImg = v.findViewById(R.id.imgItem)
		}
	}

}
