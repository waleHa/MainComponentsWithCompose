package com.example.maincomponents.ui


import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maincomponents.MainBroadcastReceiver
import com.example.maincomponents.core.util.PermissionUtils.handlePermissionsResult
import com.example.maincomponents.core.util.PermissionUtils.loadContacts
import com.example.maincomponents.domain.ContactModel
import com.example.maincomponents.ui.theme.MainComponentsTheme


class MainActivity : ComponentActivity() {
    private val REQUEST_READ_CONTACTS: Int = 1231

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Initialize Broadcast
            val filter = IntentFilter()
            val intent = Intent("com.example.maincomponents.MainBroadcastReceiver")

            filter.addAction("com.example.maincomponents.MainBroadcastReceiver")
            registerReceiver(MainBroadcastReceiver(), filter, RECEIVER_EXPORTED)
            sendBroadcast(intent)
            MainComponentsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AlignYourContactColumn(
                            requireNotNull(
                                loadContacts(
                                    this@MainActivity,
                                    REQUEST_READ_CONTACTS
                                )
                            )
                        )

                        // Initialize Service Button
                        ServiceToggleButton(this@MainActivity)
                    }
                }
            }
        }
    }

    @Composable
    fun Contact(contact: ContactModel, modifier: Modifier = Modifier) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(16.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = "ID: ${contact.id ?: "N/A"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(8.dp))

                SelectionContainer {
                    Text(
                        text = "Name: ${contact.name ?: "N/A"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(8.dp))
                SelectionContainer {
                    Text(
                        text = "Phone: ${contact.phoneNumber ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(8.dp))
                SelectionContainer {
                    Text(
                        text = "Email: ${contact.email ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
    @Composable
    fun AlignYourContactColumn(
        usersList: List<ContactModel>,
        modifier: Modifier = Modifier
    ) {
        // Implement composable here
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = modifier
        ) {
            items(usersList.sortedBy { it.id }) { item ->
                Contact(
                    contact = item,
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ContactPreview(){
        Contact(ContactModel("123","Ali","333333","ali@ali.com"))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handlePermissionsResult(
            this,
            requestCode,
            permissions,
            grantResults,
            REQUEST_READ_CONTACTS
        )
    }

}
