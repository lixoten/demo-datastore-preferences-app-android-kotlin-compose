package com.lixoten.datastorepreferences

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.lixoten.datastorepreferences.data.UserPreferences
import com.lixoten.datastorepreferences.data.UserPreferencesRepository
import com.lixoten.datastorepreferences.ui.theme.DataStorePreferencesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DataStorePreferencesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Main()
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Main() {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Create an instance of the UserPreferencesRepository
    val userPreferencesRepository by lazy { UserPreferencesRepository(context) }

    val userPreferences by userPreferencesRepository.userPreferencesFlow.collectAsState(initial = UserPreferences())

    // This is a String
    var firstName by remember {
        mutableStateOf("")
    }

    // This is a TextFieldValue
    var lastName by remember {
        mutableStateOf(TextFieldValue())
    }

    var age by remember {
        mutableStateOf(TextFieldValue())
    }

    var toggleIcon by remember {
        mutableStateOf(false)
    }

    // Load/reload the saved preferences on init composition and recompositions
    LaunchedEffect(userPreferences) {
        toggleIcon = userPreferences.toggleIcon
        firstName = TextFieldValue(userPreferences.firstName).text
        lastName = TextFieldValue(userPreferences.lastName)
        age = TextFieldValue(userPreferences.age.toString())
        Log.d("MYTAG", lastName.text)
    }

    //userPreferences
    val tokenFirstName = userPreferences.firstName
    val tokenLastName = userPreferences.lastName
    val tokenAge = userPreferences.age
    val toggleIconText = userPreferences.toggleIcon


    Column(
        modifier = Modifier.clickable { keyboardController?.hide() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Text(text = "DataStore Preference Example", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = tokenFirstName)
        Text(text = tokenLastName)
        Text(text = tokenAge.toString())
        Text(text = toggleIconText.toString())

        Spacer(modifier = Modifier.height(15.dp))

        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Grid or View: ")
                IconButton(
                    onClick = {
                        toggleIcon = !toggleIcon
//                        scope.launch {
//                            userPreferencesRepository.updateUserPreferencesIcon(
//                                toggleIcon
//                            )
//                        }
                    }
                ) {
                    Icon(
                        imageVector = if (toggleIcon) Icons.Default.GridView else Icons.Default.ViewList,
                        contentDescription = null
                    )
                }

            }

            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text(text = "First Name") }
            )

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text(text = "Last Name") }
            )

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                value = age,
                onValueChange = { age = it },
                label = { Text(text = "Age(numeric)") }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    userPreferencesRepository.updateUserPreferences(
                        firstName,
                        lastName.text,
                        age.text.toInt(),
                    )
                    userPreferencesRepository.updateUserPreferencesIcon(
                        toggleIcon
                    )
                }
            }
        ) {
            Text(text = "Save Settings")
        }
    }
}