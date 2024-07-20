// Assuming your  package is defined as follows
package com.example.myapplication

// Other necessary imports

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.GeneratingTokens
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposedemo.ml.FullModel
import com.example.jetpackcomposedemo.ml.PrunedModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

val models = arrayOf("Full Model", "Pruned Model")
val REJ_SCORE = 0.6f
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
        }

        super.onCreate(savedInstanceState)


        setContent {
            // Use the default MaterialTheme
            MaterialTheme {
                // Create a NavController
                val navController = rememberNavController()
                var screen by remember {
                    mutableStateOf(0)
                }
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
                            Detection(this@MainActivity)
                        }
                        
                        composable("Evaluation") {
                            Evaluation(this@MainActivity)
                        }
                        
                    }

                    //USER BOTTOMBAR
                    BottomNavigation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    backgroundColor = Color.White
                    ) {
                        // Home Page
                        BottomNavigationItem(
                            selected = screen == 0,
                            onClick = {
                                navController.navigate("Detection")
                                screen = 0
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
                            selected = screen == 1,
                            onClick = {
                                navController.navigate("Evaluation")
                                screen = 1
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.GeneratingTokens,
                                    contentDescription = "Evaluation"
                                )
                            },
                            label = {
                                Text(text = "Evaluation")
                            }
                        )
                    }

                }
            }

        }

    }
}

@Composable
fun Evaluation(context: Context) {
    var progress by remember { mutableStateOf(0f) }
    var folderProgress by remember { mutableStateOf(0f)}
    var TotalTimeTaken by remember { mutableStateOf((0).toDouble()) }

    var result by remember { mutableStateOf<Double>((0).toDouble()) }
    var canEvaluate by remember { mutableStateOf<Boolean>(true) }
    var expanded by remember { mutableStateOf(false) }
    var modelChoosed  by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            text = "Evaluation",
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
                Button(
                    enabled = canEvaluate,
                    onClick = {
                        canEvaluate = false
                        CoroutineScope(Dispatchers.Default).launch {
                            PerformanceEvaluation(context, modelChoosed).collect {(timeValues, prog, imgProg) ->
                                val (avgTim, totTime) = timeValues
                                withContext(Dispatchers.Main) {
                                    if(avgTim != (0).toDouble()){
                                        result = avgTim
                                        progress= prog
                                        canEvaluate = true
                                        TotalTimeTaken = totTime
                                    }else{
                                        progress = prog
                                        folderProgress = imgProg
                                    }
                                }
                            }
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Start Evaluation")

                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.4f)
                    .border(width = 2.dp, color = Color.Gray), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Localized description")
                    }
                    Text(text = models[modelChoosed],style = MaterialTheme.typography.h5.copy(

                            fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    ),)
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier
                        .fillMaxSize()
                        .border(width = 2.dp, color = Color.Gray)) {
                        DropdownMenuItem(
                            text = { Text("Full Model") },
                            onClick = { modelChoosed = 0
                                expanded = false
                                      },
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Pruned Model") },
                            onClick = { modelChoosed = 1
                                        expanded = false
                                      },
                            )
                    }
                }
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .border(width = 2.dp, color = Color.Gray)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if(canEvaluate && result == (0).toDouble()){
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "Generation",
                    Modifier.size(50.dp)
                )
                Text(text = "Result will appear here",
                    style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),)
            }
            else if(canEvaluate && result != (0).toDouble()){
                Text(text = ("Mean eval time: "+ String.format("%.5f", result) + "seconds"),
                    style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),)
                Text(text = ("Total time taken: " + String.format("%.2f", TotalTimeTaken) + " seconds"),
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),)
            }else
            {
                //processing images
                Text(text = ("folder progress ${progress*100}%"), 
                    style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),)
                Text(text = ("image progress ${folderProgress*100}%"),
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),)
                Spacer(modifier = Modifier.size(15.dp))
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                )
            }

        }
    }
}

fun PerformanceEvaluation(context: Context, modelC: Int) : kotlinx.coroutines.flow.Flow<Triple<Pair<Double,Double>, Float, Float>> = flow{

    var totalTime = (0).toDouble()
    var count = 0
    var folderCount = 0
    val list= context.assets.list("test")
    var progress = 0f;


    list?.forEach { file ->
        Log.d("IMG","processing folder: $file")

        val imageList = context.assets.list("test/$file/")

        folderCount = 0

        imageList?.forEach { image ->

            emit(Triple<Pair<Double,Double>,Float,Float>(Pair<Double,Double>((0).toDouble(),(0).toDouble()), progress/list.count(), folderCount.toFloat()/imageList.count().toFloat()))

            Log.d("IMG","processing image: $image")
            val img = context.assets.open("test/$file/$image")
            val _bitmap = BitmapFactory.decodeStream(img)
            val bitmap =  Bitmap.createScaledBitmap(_bitmap, 224, 224, false);

            val (cat, time) = Evaluate(bitmap, context, modelC)
            totalTime += time
            count += 1
            folderCount+=1
        }

        progress += 1f
    }
    emit(Triple<Pair<Double,Double>,Float,Float>(Pair<Double,Double>(totalTime/count, totalTime), 1f, 1f))
}


@Composable
fun Detection(_context: Context) {
    var pictureUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var result by remember { mutableStateOf<String>("")  }
    var TimeTaken by remember { mutableStateOf<Double >((0).toDouble()) }
    var expanded by remember { mutableStateOf(false) }
    var modelChoosed  by remember { mutableStateOf(0) }
    var evaluating by remember {
        mutableStateOf(false)
    }
    var context = _context

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
            bitmap = null
            result = ""
            pictureUri = uri
            if(uri != null) {
                val _bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                //image resize
                //val resized = Glide.with(context).asBitmap().load(bitmap).apply(RequestOptions().override(224, 224)).submit().get();

                bitmap =  Bitmap.createScaledBitmap(_bitmap, 224, 224, false);
        }}, context)

        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.1f)
            .border(width = 2.dp, color = Color.Gray), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Localized description")
            }
            Text(text = models[modelChoosed],style = MaterialTheme.typography.h5.copy(

                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            ),)
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier
                .fillMaxSize()
                .border(width = 2.dp, color = Color.Gray)) {
                DropdownMenuItem(
                    text = { Text("Full Model") },
                    onClick = { modelChoosed = 0
                        expanded = false
                    },
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Pruned Model") },
                    onClick = { modelChoosed = 1
                        expanded = false
                    },

                    )
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.8f)
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
                if(result == "")
                    Text(
                        text = "Results will display here",
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        ),
                    )
                else if(evaluating){
                    Text(text = ("Evaluating..."),
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),)
                    Spacer(modifier = Modifier.size(15.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                    )
                }
                else {

                    Text(
                        text = "Class: $result",

                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = "Time taken: "+ String.format("%.5f", TimeTaken)  +" seconds",

                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                    )
                }
            }

            if(result == "")
                Button(
                    enabled = pictureUri != null,
                    onClick = {
                        CoroutineScope(Dispatchers.Default).launch {
                            bitmap?.let {
                                evaluating = true
                                val (category, timeTaken) = Evaluate(it, context, modelChoosed)
                                withContext(Dispatchers.Main) {
                                    result = category
                                    TimeTaken = timeTaken
                                    Log.d("IMG",TimeTaken.toString())
                                }
                            }
                            evaluating = false
                        }

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


fun _Evaluate(resized: Bitmap, context: Context, model: FullModel): Pair<String,Double> {
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
    val buffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
    buffer.order(ByteOrder.nativeOrder())

    val intValues = IntArray(resized.width * resized.height)
    val mutableBitmap = resized.copy(Bitmap.Config.RGBA_F16, true)

    mutableBitmap.getPixels(intValues, 0, mutableBitmap.width, 0, 0,  mutableBitmap.width,  mutableBitmap.height)

    var pixel = 0
    for (i in 0..223) {
        for (j in 0..223) {
            val pixVal = intValues[pixel++]
            buffer.putFloat((`pixVal` shr 16 and 0xFF) * (1f / 127.5f) - 1)
            buffer.putFloat((`pixVal` shr 8 and 0xFF) * (1f / 127.5f) - 1)
            buffer.putFloat((`pixVal` and 0xFF) * (1f / 127.5f) - 1)
        }
    }

    inputFeature0.loadBuffer(buffer)

    val startTime = System.nanoTime()


    var outputs = model.process(inputFeature0)
    val endTime = System.nanoTime()

    val totalTime= (endTime - startTime).toDouble()/1000000000L

    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

    val result = outputFeature0.floatArray
    val argmax = result.withIndex().maxByOrNull { it.value }?.index
    Log.d("MODEL",result[argmax!!].toString())

    var classes = arrayOf("CompVis_stable-diffusion-v1-4", "DeepFloyd_IF-II-L-v1.0", "Real", "stabilityai_stable-diffusion-2-1-base", "stabilityai_stable-diffusion-xl-base-1.0")


    model.close()
    if(result[argmax!!]> REJ_SCORE)
        return(Pair<String, Double>(classes[argmax!!], totalTime))
    else
        return(Pair<String, Double>("UNKOWN", totalTime))
}

fun _Evaluate(resized: Bitmap, context: Context, model: PrunedModel): Pair<String,Double> {
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
    val buffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
    buffer.order(ByteOrder.nativeOrder())

    val intValues = IntArray(resized.width * resized.height)
    val mutableBitmap = resized.copy(Bitmap.Config.RGBA_F16, true)

    mutableBitmap.getPixels(intValues, 0, mutableBitmap.width, 0, 0,  mutableBitmap.width,  mutableBitmap.height)

    var pixel = 0
    for (i in 0..223) {
        for (j in 0..223) {
            val pixVal = intValues[pixel++]
            buffer.putFloat((`pixVal` shr 16 and 0xFF) * (1f / 127.5f) - 1)
            buffer.putFloat((`pixVal` shr 8 and 0xFF) * (1f / 127.5f) - 1)
            buffer.putFloat((`pixVal` and 0xFF) * (1f / 127.5f) - 1)
        }
    }

    inputFeature0.loadBuffer(buffer)

    val startTime = System.nanoTime()


    var outputs = model.process(inputFeature0)
    val endTime = System.nanoTime()

    val totalTime= (endTime - startTime).toDouble()/1000000000L

    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

    val result = outputFeature0.floatArray
    val argmax = result.withIndex().maxByOrNull { it.value }?.index
    var classes = arrayOf("CompVis_stable-diffusion-v1-4", "DeepFloyd_IF-II-L-v1.0", "Real", "stabilityai_stable-diffusion-2-1-base", "stabilityai_stable-diffusion-xl-base-1.0")

    Log.d("MODEL RESULT", classes[argmax!!])
    model.close()

    return(Pair<String, Double>(classes[argmax!!], totalTime))
}
fun Evaluate(resized: Bitmap, context: Context, modelC: Int): Pair<String,Double> {
    if(modelC == 0){
       return(_Evaluate(resized, context, FullModel.newInstance(context)))
    }
    else{
        return(_Evaluate(resized, context, PrunedModel.newInstance(context)))
    }
}

@Composable
fun ImageUploadButton(onImageSelected: (Uri?) -> Unit, context: Context) {
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

            val _bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver,
                    imageUri!!
                ))
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }

            //image resize
            //val resized = Glide.with(context).asBitmap().load(bitmap).apply(RequestOptions().override(224, 224)).submit().get();

            val resized =  Bitmap.createScaledBitmap(_bitmap, 224, 224, false);

            Image(
                bitmap = resized.asImageBitmap(),
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
                    Text(
                        "Change",
                        maxLines = 1,
                        style = MaterialTheme.typography.h5.copy(fontSize = 13.sp)
                    )
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
                    Text(
                        "Remove",
                        maxLines = 1,
                        style = MaterialTheme.typography.h5.copy(fontSize = 13.sp)
                    )
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

