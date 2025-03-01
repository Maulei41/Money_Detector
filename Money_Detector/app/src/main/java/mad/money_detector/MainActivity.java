package mad.money_detector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import mad.money_detector.ml.ModelUnquant;

public class MainActivity extends AppCompatActivity {
    Button camera;
    ImageButton aboutme;
    int imageSize = 224;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        camera = findViewById(R.id.button);
        aboutme = findViewById(R.id.imageButton);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
                }
            }
        });

        aboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, aboutMe.class);
                startActivity(intent);
            }
        });
    }

    public void classifyImage(Bitmap image, byte[] byteArray){
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer =ByteBuffer.allocateDirect(4 * imageSize * imageSize *3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues,0, image.getWidth(),0,0,image.getWidth(),image.getHeight());
            int pixel = 0;
            for(int i = 0; i< imageSize; i++){
                for(int j =0; j< imageSize; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val>>16)&0xFF)/255.0f);
                    byteBuffer.putFloat(((val >> 8)&0xFF)/255.0f);
                    byteBuffer.putFloat((val&0xFF)/255.0f);
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            float atleaseConfidence = 0.9f;
            for (int i = 0; i < confidences.length;i++){
                if (confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            if (maxConfidence < atleaseConfidence){
                maxPos = 45;
            }
            String[]classes = {"20"
                    ,"50"
                    ,"100",
                    "500",
                    "1000",
                    "20"
                    ,"50"
                    ,"100",
                    "500",
                    "1000",
                    "20"
                    ,"50"
                    ,"100",
                    "500",
                    "1000",
                    "20"
                    ,"50"
                    ,"100",
                    "500",
                    "1000",
                    "20"
                    ,"50"
                    ,"100",
                    "500",
                    "1000",
                    "20"
                    ,"50"
                    ,"100",
                    "500",
                    "1000",
                    "20"
                    ,"50"
                    ,"100",
                    "500",
                    "1000",
                    "20"
                    ,"50"
                    ,"100",
                    "500",
                    "1000",
                    "20"
                    ,"50"
                    ,"100",
                    "500",
                    "1000",
                    "Missing"};
            String result = classes[maxPos];
            // Releases model resources if no longer used.
            model.close();
            switch (result) {
                case "100": {
                    Intent intent = new Intent(this, hundredActivity.class);
                    intent.putExtra("image",byteArray);
                    startActivity(intent);

                    break;
                }
                case "500": {
                    Intent intent = new Intent(this, fivehundredActivity.class);
                    intent.putExtra("image",byteArray);
                    startActivity(intent);
                    break;
                }
                case "20": {
                    Intent intent = new Intent(this, twentyActivity.class);
                    intent.putExtra("image",byteArray);
                    startActivity(intent);
                    break;
                }
                case "50": {
                    Intent intent = new Intent(this, fiftyActivity.class);
                    intent.putExtra("image",byteArray);
                    startActivity(intent);
                    break;
                }
                case "1000": {
                    Intent intent = new Intent(this, thousandsActivity.class);
                    intent.putExtra("image",byteArray);
                    startActivity(intent);
                    break;
                }
                default: {
                    Intent intent = new Intent(this, MissingActivity.class);
                    intent.putExtra("image",byteArray);
                    startActivity(intent);
                    break;
                }
            }
        } catch (IOException e) {
            Log.e("ModelClassification","Error loading model", e);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                Bitmap bitmap = image;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();
                image = Bitmap.createScaledBitmap(image,imageSize,imageSize,false);
                classifyImage(image, byteArray);

            }

        }
    }

}