package com.igrogood.superchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.igrogood.superchat.utilits.AUTH
import com.igrogood.superchat.utilits.initFirebase

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		savedInstanceState?: supportFragmentManager.beginTransaction()
			.replace(R.id.container, MainFragment())
			.commit()

		initFirebase()
	}

	override fun onBackPressed() {
		val fm = supportFragmentManager
		for (frag in fm.fragments) {
			if (frag.isVisible) {
				val childFm = frag.childFragmentManager
				if (childFm.backStackEntryCount > 0) {
					childFm.popBackStack()
					return
				}
			}
		}
		super.onBackPressed()
	}
}