package com.cs6018.canvasexample.ui.components


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseUser

@Composable
fun AuthenticationScreen(
    createUserWithEmailAndPassword: (String, String, (FirebaseUser?) -> Unit, () -> Unit) -> Unit,
    signInWithEmailAndPassword: (String, String, (FirebaseUser?) -> Unit, () -> Unit) -> Unit,
    navigateToDrawingList: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Drawing App!",
            style = TextStyle(fontSize = 24.sp),
            modifier = Modifier
                .padding(16.dp)
        )
        TextField(
            value = email,
            singleLine = true,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
            placeholder = { Text("example@gmail.com") }
        )

        TextField(
            value = password,
            singleLine = true,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Password") },
            placeholder = { Text("********") },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val visibilityIcon =
                        if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sign Up Button
            Button(
                onClick = {
                    createUserWithEmailAndPassword(email, password, { user ->
                        Toast.makeText(
                            context,
                            "Welcome to Drawing App, ${user?.email}",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        navigateToDrawingList()
                    }, {
                        Toast.makeText(context, "Sign up failed", Toast.LENGTH_LONG).show()
                    })
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text("Sign Up")
            }

            // Sign In Button
            Button(
                onClick = {
                    signInWithEmailAndPassword(email, password, { user ->
                        Toast.makeText(
                            context,
                            "Welcome Back, ${user?.email}",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        navigateToDrawingList()
                    }, {
                        Toast.makeText(context, "Sign In failed", Toast.LENGTH_LONG).show()
                    })
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text("Sign In")
            }
        }

    }
}

@Preview
@Composable
fun AuthenticationScreenPreview() {
    MaterialTheme {
        AuthenticationScreen({ _, _, _, _ -> }, { _, _, _, _ -> }, {})
    }
}
