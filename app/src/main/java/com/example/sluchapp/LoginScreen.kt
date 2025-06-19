package com.example.sluchapp

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            account?.idToken?.let { idToken ->
                viewModel.firebaseAuthWithGoogle(idToken)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Błąd logowania: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> onLoginSuccess()
            is LoginState.Error -> {
                Toast.makeText(context, (loginState as LoginState.Error).message, Toast.LENGTH_SHORT).show()
                // opcjonalnie reset stanu lub inne działanie
            }
            else -> { /* Idle i Loading nic nie robią */ }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (loginState == LoginState.Loading) {
            Text("Logowanie...")
        } else {
            Button(onClick = {
                launcher.launch(viewModel.getGoogleSignInIntent())
            }) {
                Text(text = "Zaloguj się przez Google")
            }
        }
    }
}
