package com.example.ui.main.jetpack

import android.content.Intent
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.java.R
import com.example.ui.main.activity.Login
import com.example.ui.main.activity.TimerActivity
import com.example.ui.main.sharedpreference.PreferManager
import com.example.ui.main.viewmodel.LoginViewModel

data class loginModel(
    val username: String,
    val password: String,
    val expiresInMins : Int
)


@Composable
fun LoginScreen(modifier: Modifier = Modifier)
{
    val viewModel : LoginViewModel = viewModel()
    val context = LocalContext.current
    var isLoading by remember {mutableStateOf(false)}
    val username = viewModel.username
    var password = viewModel.password
    var passwordVisible by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF11192C)

    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.loginlogo)
                , contentDescription = "Login Logo",
                modifier = Modifier
                    .padding(5.dp)
                    .width(180.dp)
                    .height(180.dp)
            )
            Text(
                text = "Welcome Back"
                , modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
            Text(
                text = "Sign in to continue your journey",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = modifier.padding(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF324154)
                )
            ) {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Username",
                        modifier = modifier.padding(18.dp,16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    OutlinedTextField(
                        value = username.value,
                        onValueChange = {viewModel.username.value = it},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp, 0.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF324154),
                            unfocusedContainerColor = Color(0xFF324154),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Black
                        ),
                        placeholder ={
                            Text(text = "Username", color = Color.Gray)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Username",
                                tint = Color(0xFF3F52C8)
                            )
                        }
                    )

                    Text(
                        text = "Password",
                        modifier = modifier.padding(15.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    OutlinedTextField(
                        value =password.value,
                        onValueChange = {password.value = it},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp, 0.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF324154),
                            unfocusedContainerColor = Color(0xFF324154),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Black
                        ),
                        placeholder = {
                            Text(text = "Password", color = Color.Gray)
                        },
                        visualTransformation = if (passwordVisible)
                        {
                            VisualTransformation.None
                        }
                        else{
                            PasswordVisualTransformation()
                        },
                        trailingIcon ={
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Icon(
                                    tint = Color(0xFF3F52C8),
                                            imageVector = if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = Color(0xFF3F52C8)
                            )
                        }
                    )

                    Text(
                        text = "Forgot Password",
                        textAlign = TextAlign.End,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF3F52C8)
                    )


                    Button(
                        onClick = {
                            if(username.value.isEmpty() || password.value.isEmpty())
                            {
                                makeText(context, "Fill all the fields", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                isLoading = true
                                val login = loginModel(username.value,password.value,30)
                                val response = LoginViewModel().login(login,onResult = { success ->
                                    if (success) {
                                        PreferManager.saveLogin(context, true)
                                        val intent = Intent(context, TimerActivity::class.java)
                                        context.startActivity(intent)
                                        isLoading = false
                                        (context as Login).finish()
                                    } else {
                                        makeText(context, "invalid username or password", Toast.LENGTH_SHORT).show()
                                        isLoading = false
                                    }
                                })
                            }
                        },
                        enabled = !isLoading,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                            .size(44.dp),
                        shape = RoundedCornerShape(10.dp)
                        ,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3F52C8)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = "Attendance",
                                tint = Color(0xFF6D9EFF),
                                modifier = modifier.padding(5.dp, 0.dp)
                            )
                            Text(
                                text = "Login",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                }
            }
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    textAlign = TextAlign.Center,
                    text = "Don't have an account?",
                    color = Color.White,
                )
                Text(
                    textAlign = TextAlign.Start,
                    text = " Sign Up",
                    color = Color(0xFF3F52C8)
                )
            }
        }
    }
}


@Preview
@Composable
fun LoginScreenPreview()
{
    LoginScreen()
}