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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.cs6018.canvasexample.utils.isValidEmail
import com.cs6018.canvasexample.utils.isValidPassword
import com.google.firebase.auth.FirebaseUser


// TODO: bonus task -> sign in with Google
@Composable
fun AuthenticationScreen(
    createUserWithEmailAndPassword: (String, String, (FirebaseUser?) -> Unit, () -> Unit) -> Unit,
    signInWithEmailAndPassword: (String, String, (FirebaseUser?) -> Unit, () -> Unit) -> Unit,
    navigateToDrawingList: () -> Unit,
    preloadCurrentUserDrawingHistory: (String) -> Unit,
) {
    val invalidPasswordTooltip =
        "Password must include at least one uppercase letter, one lowercase letter, one digit, one special character, be at least 8 characters long, and contain no spaces."
    val invalidEmailTooltip = "Invalid email address"
    var email by rememberSaveable { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordError by remember { mutableStateOf(false) }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    val preCheckEmailError = {
        isEmailError = !isValidEmail(email)
    }

    val preCheckPasswordError = {
        isPasswordError = !isValidPassword(password)
    }

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

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 56.dp),
            value = email,
            singleLine = true,
            onValueChange = {
                email = it
                preCheckEmailError()
            },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email", tint = if (isEmailError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground) },
            placeholder = { Text("example@gmail.com") },
            isError = isEmailError,
            supportingText = {
                if (isEmailError) {
                    Text(
                        text = invalidEmailTooltip,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            trailingIcon = {
                if (isEmailError)
                    Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 56.dp),
            value = password,
            singleLine = true,
            onValueChange = {
                password = it
                preCheckPasswordError()
            },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Password", tint = if (isPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground) },
            placeholder = { Text("********") },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val visibilityIcon =
                        if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(
                        imageVector = visibilityIcon,
                        contentDescription = description,
                        tint = if (isPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            isError = isPasswordError,
            supportingText = {
                if (isPasswordError) {
                    Text(
                        text = invalidPasswordTooltip,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sign Up Button
            Button(
                onClick = {
                    if (!isEmailError && !isPasswordError) {
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
                    }
                },
                enabled = email.isNotEmpty() && password.isNotEmpty() && !isEmailError && !isPasswordError,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 65.dp, end = 15.dp)
            ) {
                Text("Sign Up")
            }

            // Log In Button
            Button(
                onClick = {
                    if (!isEmailError && !isPasswordError) {
                        signInWithEmailAndPassword(email, password, { user ->
                            Toast.makeText(
                                context,
                                "Welcome Back, ${user?.email}",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            preloadCurrentUserDrawingHistory(user?.uid ?: "")
                            navigateToDrawingList()
                        }, {
                            Toast.makeText(context, "Log In failed", Toast.LENGTH_LONG).show()
                        })
                    }
                },
                enabled = email.isNotEmpty() && password.isNotEmpty() && !isEmailError && !isPasswordError,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp, end = 65.dp)
            ) {
                Text("Log In")
            }
        }
    }
}

@Preview
@Composable
fun AuthenticationScreenPreview() {
    MaterialTheme {
        AuthenticationScreen({ _, _, _, _ -> }, { _, _, _, _ -> }, {}, {})
    }
}
