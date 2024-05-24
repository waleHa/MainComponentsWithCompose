package com.example.maincomponents.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.maincomponents.domain.ContactModel
import com.example.maincomponents.ui.MainActivity
import com.google.android.material.snackbar.Snackbar


object PermissionUtils {
    @SuppressLint("Range")
    fun getContactList(context: Context): List<ContactModel> {
        val contactModels = mutableListOf<ContactModel>()
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
                            val id =
                                pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                            val email =
                                pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))


                            val contactModel = ContactModel(id, name, phoneNumber, email)
                            contactModels.add(contactModel)
                        }
                    }
                    phoneCursor?.close()
                } else {
                    val contactModel = ContactModel(name, null, null, null)
                    contactModels.add(contactModel)
                }
            }
        }
        cursor?.close()
        return contactModels
    }

    private fun requestContactsPermission(activity: MainActivity, requestCode: Int) {
        // Check if the permission has already been granted
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission has already been granted, do something with the contact list
            val contacts = getContactList(activity)
            Log.d("Contacts", contacts.joinToString(separator = "\n"))
            Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
            // Do something with the contact list
        } else {
            // Permission has not been granted, request it
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                // Explain why the app needs the permission
                // You can show a dialog or a Snackbar here
                Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    "The app needs permission to access your contacts.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("OK") {
                    // Request the permission
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        requestCode
                    )
                }.show()
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    requestCode
                )
            }
        }
    }


    fun loadContacts(activity: MainActivity, REQUEST_READ_CONTACTS: Int): List<ContactModel>? {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val contacts = getContactList(activity)
            Log.d("Contacts", contacts.joinToString(separator = "\n"))
            return contacts

//            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
        } else {
            requestContactsPermission(activity, REQUEST_READ_CONTACTS);
            Toast.makeText(activity, "Permission!", Toast.LENGTH_SHORT).show()
        }
        return null
    }


    fun handlePermissionsResult(
        activity: MainActivity,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        expectedRequestCode: Int
    ) {
        if (requestCode == expectedRequestCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission has been granted, do something with the contact list
            val contacts = getContactList(activity)
            Log.d("Contacts", contacts.joinToString(separator = "\n"))
            Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
            // Do something with the contact list
        } else {
            // Permission has been denied
            // You can show a dialog or a Snackbar here to explain why the app needs the permission
        }
    }
}