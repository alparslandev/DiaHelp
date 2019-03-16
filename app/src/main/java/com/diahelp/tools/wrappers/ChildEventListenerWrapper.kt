package com.diahelp.tools.wrappers

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

interface ChildEventListenerWrapper : ChildEventListener {
    override fun onCancelled(databaseError: DatabaseError) {}
    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {}
    override fun onChildRemoved(dataSnapshot: DataSnapshot) { }
}