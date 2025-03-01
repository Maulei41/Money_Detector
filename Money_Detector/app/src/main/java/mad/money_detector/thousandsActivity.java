package mad.money_detector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class thousandsActivity extends AppCompatActivity {
    ImageView imageView, imageView2;
    ImageButton aboutme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thousands);
        aboutme = findViewById(R.id.imageButton);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        imageView.setImageResource(R.drawable.thousands);
        imageView2.setImageBitmap(image);
        try{
            Thread.sleep(700);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
        MediaPlayer mediaPlayer = MediaPlayer.create(thousandsActivity.this, R.raw.thousand);
        mediaPlayer.start();
        aboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thousandsActivity.this, aboutMe.class);
                startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}