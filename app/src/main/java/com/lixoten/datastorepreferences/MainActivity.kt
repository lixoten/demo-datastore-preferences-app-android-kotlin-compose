package com.lixoten.datastorepreferences

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val userPreferences by userPreferencesRepository.userPreferencesFlow.collectAsState(initial = UserPreferences() )
    val scope = rememberCoroutineScope()


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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Text(text = "Data Storage Example", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = tokenFirstName)
        Text(text = tokenLastName)
        Text(text = tokenAge.toString())
        Text(text = toggleIconText.toString())

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(text = "First Name")}
        )

        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(text = "Last Name")}
        )

        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text(text = "Age(numeric)")}
        )

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    userPreferencesRepository.updateUserPreferences(
                        firstName,
                        lastName.text,
                        age.text.toInt(),
                        //toggleIcon,
                    )
                }
            }
        ) {
            Text(text = "Update")
        }

        IconButton(
            onClick = {
                toggleIcon = !toggleIcon
                scope.launch {
                    //userPreferencesRepository.saveLayoutPreference(isLinearLayout)
                    userPreferencesRepository.updateUserPreferencesIcon(
                        toggleIcon
                    )
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                tint = if (toggleIcon) Color.Red else Color.LightGray,
                contentDescription = null
            )

        }
    }
}