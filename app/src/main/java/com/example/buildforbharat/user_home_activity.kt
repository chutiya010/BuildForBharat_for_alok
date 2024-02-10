package com.example.buildforbharat

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buildforbharat.databinding.ActivityUserHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class user_home_activity : AppCompatActivity() {
    private lateinit var binding: ActivityUserHomeBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var pincodeReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    val SHARED_PREF : String = "sharedPrefs"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        val userkey: String? = sharedPreferences.getString("username", "")
        pincodeReference = FirebaseDatabase.getInstance().reference.child("pincodes")
        auth = FirebaseAuth.getInstance()
        binding.addButton.setOnClickListener {
            showAddPhoneNumberDialog(userkey.toString())
        }
        binding.logout.setOnClickListener {
            val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREF,
                MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("name","false")
            editor.putString("username","")
            editor.apply()
            val intent = Intent(this, LandingPage::class.java)
            startActivity(intent)
            finish()
        }
        userAdapter = UserAdapter()
        recyclerView = findViewById(R.id.OwnerRoomlist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = userAdapter
        /////////////////////////

        ////////////////////////

    }

    private fun showAddPhoneNumberDialog(username: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add PINCODE")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_PHONE
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ ->
            // Call a function to add the phone number to Cloud Firestore.
            //pincodeReference = pincodeReference.child(input.text.toString())
            Toast.makeText(this, "HAHAH", Toast.LENGTH_SHORT).show()
            addPhoneNumberToFirestore(input.text.toString(),username)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun addPhoneNumberToFirestore(pincode: String, username: Any) {
        pincodeReference.child(pincode).orderByChild("timestamp").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Merchant::class.java)
                if (message != null) {
                    // Display the message in your UI
                    userAdapter.addMerchant(message)
                    recyclerView.scrollToPosition(userAdapter.itemCount - 1)

                }else{
                    Toast.makeText(this@user_home_activity, "fail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}