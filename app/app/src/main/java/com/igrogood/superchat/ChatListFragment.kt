package com.igrogood.superchat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.igrogood.superchat.utilits.NODE_USERS
import com.igrogood.superchat.utilits.REF_DATABASE_ROOT
import com.igrogood.superchat.utilits.showToast

class ChatListFragment: Fragment(R.layout.fragment_chat_list){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.newChatBtn).setOnClickListener {
            val userName = view.findViewById<EditText>(R.id.newChatEditText).text.toString()
            val user = REF_DATABASE_ROOT.child(NODE_USERS).child(userName).ref
            showToast(user.toString())
            REF_DATABASE_ROOT.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            //showToast(user.toString())
            //REF_DATABASE_ROOT.child(NODE_CHATS)
        }
    }
}