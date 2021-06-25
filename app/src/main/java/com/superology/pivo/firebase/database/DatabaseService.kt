package com.superology.pivo.firebase.database

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.superology.pivo.R
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject

object DatabaseService {

    private val TAG = DatabaseService::class.java.canonicalName
    private val dataSubject = ReplaySubject.create<String>(1)
    private val dbRef by lazy { FirebaseDatabase.getInstance().reference }

    fun observeData(): Observable<String> {

        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                dataSubject.onNext(snapshot.value as String)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message, error.toException())
            }
        })
        return dataSubject.subscribeOn(Schedulers.newThread())
    }

    fun setMessage(context: Context?, message: String) {
        dbRef.setValue(message)
            .addOnFailureListener { Log.e(TAG, context?.getString(R.string.error_firebase_set_element) ?: "") }
    }
}