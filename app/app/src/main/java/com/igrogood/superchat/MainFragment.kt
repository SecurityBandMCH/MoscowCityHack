package com.igrogood.superchat

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.igrogood.superchat.utilits.AUTH

class MainFragment: Fragment(R.layout.fragment_main), ILoadForm {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance = true
        if (AUTH.currentUser==null)
            authorizationForm()
        else {
            setHasOptionsMenu(true)
            chatListForm()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_quit -> {
                AUTH.signOut()
                authorizationForm()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun chatListForm() {
        childFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, ChatListFragment())
            .commit()
    }

    override fun authorizationForm() {
        childFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, AuthorizationFragment())
            .commit()
    }
}