// Assuming your  package is defined as follows
package com.example.myapplication

// Other necessary imports

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.GeneratingTokens
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter





class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Use the default MaterialTheme
            MaterialTheme {
                // Create a NavController
                val navController = rememberNavController()
                var showBottomNavigation by remember { mutableStateOf("") }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Set up the NavHost with the NavController and navigation graph
                    NavHost(
                        navController = navController,
                        startDestination = "Detection"
                    ) {
                        composable("Detection") {
                            // Pass the NavController to the LoginPage
                            Detection()
                            showBottomNavigation = "user"
                        }
                        composable("Generation") {
                            Generation()
                            showBottomNavigation = "user"
                        }
                    }

                    //USER BOTTOMBAR
                    if (showBottomNavigation=="user") BottomNavigation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        backgroundColor = Color.White
                    ) {
                        // Home Page
                        BottomNavigationItem(
                            selected = navController.currentDestination?.route == "Detection",
                            onClick = {
                               navController.navigate("Detection")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = "Detect"
                                )
                            },
                            label = {
                                Text(text = "Detect")
                            }
                        )

                        // Map Page
                        BottomNavigationItem(
                            selected = navController.currentDestination?.route == "Generation",
                            onClick = {
                                navController.navigate("Generation")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.GeneratingTokens,
                                    contentDescription = "Generation"
                                )
                            },
                            label = {
                                Text(text = "Generate")
                            }
                        )
                    }

                }
            }

        }

    }
}

@Composable
fun Generation() {
    var prompt by remember { mutableStateOf<String>("") }
    var steps by remember { mutableStateOf<String>("20") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            text = "Generation",
            style = MaterialTheme.typography.h5.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            ),
            color = Color.Black,
        )


            //Result displayer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .fillMaxHeight(.4f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                OutlinedTextField(
                    label = { Text(text = "Prompt") },
                    placeholder = { Text(text = "Insert Prompt") },
                    value = prompt,
                    minLines = 3,
                    maxLines = 3,
                    onValueChange = { newText: String ->
                        prompt = newText
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = steps,
                    label = { Text(text = "Steps") },
                    placeholder = { Text(text = "Insert steps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { it ->
                        steps = it
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(

                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Generate")
                }
        }

        if(imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
            )
        } else {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .border(width = 2.dp, color = Color.Gray)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Generation"
                )
                Text(text = "Result will appear here")

            }
        }
    }

}



@Composable
fun Detection() {
    var pictureUri by remember { mutableStateOf<Uri?>(null) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            text = "Detection",
            style = MaterialTheme.typography.h5.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            ),
            color = Color.Black,
            )


        ImageUploadButton(onImageSelected = { uri ->
            pictureUri = uri
        })

        Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.7f)
            .border(width = 2.dp, color = Color.Gray)
            .padding(20.dp)
        ) {
            //Result displayer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .fillMaxHeight(.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(text = "Results will display here")
                Text(text = "Fake/real")
                Text(text = "Time taken:")
            }

            Button(
                enabled = pictureUri != null,
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Check")
            }


        }
    }

    // Add a way to upload a picture

}


@Composable
fun ImageUploadButton(onImageSelected: (Uri?) -> Unit) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Create an activity result launcher for the image picker
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            onImageSelected(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.5f)
    ) {
        // Display selected image
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalArrangement = Arrangement.Center

            ) {
                Button(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier
                        .width(150.dp)
                        .padding(top = 16.dp)
                ) {
                    Text("Change", maxLines = 1, style = MaterialTheme.typography.h5.copy(fontSize = 13.sp),)
                }

                Spacer(Modifier.width(20.dp))

                Button(
                    onClick = {
                        onImageSelected(null);
                        imageUri = null;
                    },
                    modifier = Modifier
                        .width(150.dp)
                        .padding(top = 16.dp)
                ) {
                    Text("Remove", maxLines = 1, style = MaterialTheme.typography.h5.copy(fontSize = 13.sp),)
                }
            }

        }else
        {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .border(width = 2.dp, color = Color.Gray)
                    .clickable(onClick = { launcher.launch("image/*"); }),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Generation"
                )
                Text(text = "Load an image to procede")

            }

            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()

                    .padding(top = 16.dp)
            ) {
                Text("Select Image")
            }

        }

        // Button to launch the image picker

    }
}

