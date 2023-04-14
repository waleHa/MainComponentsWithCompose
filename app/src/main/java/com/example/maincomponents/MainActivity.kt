package com.example.maincomponents

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_CONTACTS: Int = 1231
    var mobileArray = mutableListOf<String>()
    var numberArray = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize Broadcast
        val filter = IntentFilter()
        filter.addAction("com.example.maincomponents.MainBroadcastReceiver")
        registerReceiver(MainBroadcastReceiver(), filter)

        val intent = Intent("com.example.maincomponents.MainBroadcastReceiver")
        sendBroadcast(intent)


        //Initialize Service
        val button = findViewById<Button>(R.id.btnMusicPlayer)
        button.setOnClickListener {
//            loadContacts()
            if (isMyServiceRunning(MainService::class.java)) {
                button.text = getString(R.string.stopped)
                stopService(Intent(this@MainActivity, MainService::class.java))
            } else {
                button.text = getString(R.string.started)
                startService(Intent(this@MainActivity, MainService::class.java))
            }
        }


        //Initialize Content Providers
        loadContacts()


    }

    private fun loadContacts() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val contacts = getContactList(this)
            Log.d("Contacts", contacts.joinToString(separator = "\n"))
//            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
        } else {
            requestContactsPermission();
            Toast.makeText(this, "Permission!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestContactsPermission() {
        // Check if the permission has already been granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission has already been granted, do something with the contact list
            val contacts = getContactList(this)
            Log.d("Contacts", contacts.joinToString(separator = "\n"))
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            // Do something with the contact list
        } else {
            // Permission has not been granted, request it
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                // Explain why the app needs the permission
                // You can show a dialog or a Snackbar here
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "The app needs permission to access your contacts.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("OK") {
                    // Request the permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        REQUEST_READ_CONTACTS
                    )
                }.show()
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    REQUEST_READ_CONTACTS
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission has been granted, do something with the contact list
            val contacts = getContactList(this)
            Log.d("Contacts", contacts.joinToString(separator = "\n"))
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            // Do something with the contact list
        } else {
            // Permission has been denied
            // You can show a dialog or a Snackbar here to explain why the app needs the permission
        }
    }

    @SuppressLint("Range")
    fun getContactList(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber =
                    it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
                if (hasPhoneNumber) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    phoneCursor?.use { pc ->
                        while (pc.moveToNext()) {
                            val phoneNumber =
                                pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val contact = Contact(name, phoneNumber)
                            contacts.add(contact)
                        }
                    }
                    phoneCursor?.close()
                } else {
                    val contact = Contact(name, null)
                    contacts.add(contact)
                }
            }
        }
        cursor?.close()
        return contacts
    }

    data class Contact(val name: String, val phoneNumber: String?)


    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}