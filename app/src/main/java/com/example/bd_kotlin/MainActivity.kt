package com.example.bd_kotlin

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dbHelper = DatabaseOpenHelper(this)

        setContent {

            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {
                SplashScreen {
                    showSplash = false
                }
            } else {
                UserForm()
            }
        }
    }

    @Composable
    fun UserForm() {
        var name by remember { mutableStateOf("") }
        var lastname by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }

        var gender by remember { mutableStateOf("") }
        val genderOptions = listOf("Male", "Female", "Other")
        var expanded by remember { mutableStateOf(false) }

        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }

        var users by remember { mutableStateOf(dbHelper.getAllUsers()) }

        var editingUserId by remember { mutableStateOf<Int?>(null) }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(50.dp),) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label ={ Text("Name")},
                    modifier = Modifier.padding(2.dp)
                )
                TextField(
                    value = lastname,
                    onValueChange = { lastname = it },
                    label ={ Text("Lastname")},
                    modifier = Modifier.padding(2.dp)
                )
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    label ={ Text("Age")},
                    modifier = Modifier.padding(2.dp)
                )

                TextField(
                    value=gender,
                    onValueChange = {},
                    readOnly = true,
                    label = {Text("Gender")},
                    modifier= Modifier.padding(2.dp),
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded } ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand Menu")
                        }
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    genderOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text= {Text(selectionOption)},
                            onClick= {
                                gender = selectionOption
                                expanded = false
                            }
                        )
                    }
                }

                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label ={ Text("Phone")},
                    modifier = Modifier.padding(2.dp)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label ={ Text("Email")},
                    modifier = Modifier.padding(2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (editingUserId == null) {
                        if(dbHelper.insertUser(name, lastname, age.toIntOrNull() ?: 0, gender, phone, email)) {
                            Toast.makeText(this@MainActivity, "User added", Toast.LENGTH_LONG).show()
                            users = dbHelper.getAllUsers()

                            name = ""
                            lastname = ""
                            age = ""
                            gender = ""
                            phone = ""
                            email = ""
                        } else {
                            Toast.makeText(this@MainActivity, "Error adding user", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        if (dbHelper.updateUser(editingUserId!!, name, lastname, age.toIntOrNull() ?: 0, gender, phone, email)) {
                            Toast.makeText(this@MainActivity, "User updated", Toast.LENGTH_LONG).show()
                            users = dbHelper.getAllUsers()

                            name = ""
                            lastname = ""
                            age = ""
                            gender = ""
                            phone = ""
                            email = ""
                        } else {
                            Toast.makeText(this@MainActivity, "Error updating user", Toast.LENGTH_LONG).show()
                        }
                    }
                }) {
                    Text(text = if (editingUserId == null) "Add User" else "Update User")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(users) { user ->
                        UserRow(user = user,
                            onDelete = {
                                if (dbHelper.deleteUser(user["id"]!!.toInt())) {
                                    users = dbHelper.getAllUsers()
                                    Toast.makeText(this@MainActivity, "User deleted", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@MainActivity, "Error deleting user", Toast.LENGTH_LONG).show()
                                }
                            },
                            onEdit = {
                                editingUserId = user["id"]?.toIntOrNull()
                                name = user["name"] ?: ""
                                lastname = user["lastname"] ?: ""
                                age = user["age"] ?: ""
                                gender = user["gender"] ?: ""
                                phone = user["phone"] ?: ""
                                email = user["email"] ?: ""
                            }
                        )
                    }
                }

            }
        }
    }

    @Composable
    fun UserRow(user: Map<String, Any>, onDelete: () -> Unit, onEdit: () -> Unit) {
        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Text("Name: ${user["name"]}")
            Text("Lastname: ${user["lastname"]}")
            Text("Age: ${user["age"]}")
            Text("Gender: ${user["gender"]}")
            Text("Phone: ${user["phone"]}")
            Text("Email: ${user["email"]}")
            Row {
                Button(onClick = onEdit) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    @Composable
    fun SplashScreen(onTimeout: () -> Unit) {
        LaunchedEffect(Unit) {
            delay(3000)
            onTimeout()
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Splash Logo",
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    }
}