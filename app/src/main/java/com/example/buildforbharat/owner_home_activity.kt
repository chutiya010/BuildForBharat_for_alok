package com.example.buildforbharat

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buildforbharat.databinding.ActivityOwnerHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class owner_home_activity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityOwnerHomeBinding
    val SHARED_PREF : String = "sharedPrefs"
    private lateinit var merchantAdapter: MerchantAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var pincodeReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        val userkey: String? = sharedPreferences.getString("username", "")
        auth = FirebaseAuth.getInstance()
        pincodeReference = FirebaseDatabase.getInstance().reference.child("pincodes")
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
        merchantAdapter = MerchantAdapter()
        recyclerView = findViewById(R.id.OwnerRoomlist)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = merchantAdapter

        // Set up the adapter with an empty list initially
        pincodeReference.orderByChild("timestamp").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val memberId = snapshot.key
                if (memberId != null) {
                    val memberReference = pincodeReference.child(memberId)
                    memberReference.orderByChild("timestamp").addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val message = snapshot.getValue(Merchant::class.java)
                            if (message != null) {
                                if(message.username==userkey) {
                                    // Display the message in your UI
                                    merchantAdapter.addMerchant(message)
                                    recyclerView.scrollToPosition(merchantAdapter.itemCount - 1)
                                }
                            }else{
                                Toast.makeText(this@owner_home_activity, "fail", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                        override fun onChildRemoved(snapshot: DataSnapshot) {}

                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
        //////////////////////////////////////////////////

        supportActionBar?.setTitle("BuildForBharat")
        actionBar?.hide()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)
    }

    private fun showAddPhoneNumberDialog(username:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add PINCODE")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_PHONE
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ ->
            // Call a function to add the phone number to Cloud Firestore.
            Toast.makeText(this, "HAHAH", Toast.LENGTH_SHORT).show()
            addPhoneNumberToFirestore(input.text.toString(),username)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
    private fun addPhoneNumberToFirestore(pincode:String,username:String) {
        Toast.makeText(this, "KKKAKA", Toast.LENGTH_SHORT).show()
        val merchant = Merchant(pincode,username,System.currentTimeMillis())
        pincodeReference.child(pincode).push().setValue(merchant)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()
                    // Message sent successfully
                    Log.d("CHAT","Message sent successfully")
                } else {
                    // Handle the error
                    Toast.makeText(this@owner_home_activity, "Failed to send message", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
data class Merchant(val pincode: String, val username: String,val timestamp:Long) {
    constructor() : this( "", "",0)
}