package com.example.cctvplayer;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private VLCVideoLayout videoLayout;
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    private File[] images;
    private int imageIndex = 0;
    private int camIndex = 0;

    private static final int IMAGE_TIME = 8000;
    private static final int CAMERA_TIME = 25000;

    private static final String IMAGE_FOLDER = "/sdcard/slideshow/images/";

    private static final String[] CAMERAS = {
            "rtsp://admin:password@192.168.0.130:554/Streaming/Channels/102",
            "rtsp://admin:password@192.168.0.130:554/Streaming/Channels/202"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        videoLayout = findViewById(R.id.videoLayout);

        libVLC = new LibVLC(this);
        mediaPlayer = new MediaPlayer(libVLC);
        mediaPlayer.attachViews(videoLayout, null, false, false);

        File dir = new File(IMAGE_FOLDER);
        images = dir.listFiles();
        if (images != null) {
            Arrays.sort(images);
        }
        showImage();
    }

    private void showImage() {
        stopCamera();
        imageView.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.GONE);

        if (images != null && images.length > 0) {
            imageView.setImageURI(Uri.fromFile(images[imageIndex]));
            imageIndex = (imageIndex + 1) % images.length;
        }
        handler.postDelayed(this::showCamera, IMAGE_TIME);
    }

    private void showCamera() {
        imageView.setVisibility(View.GONE);
        videoLayout.setVisibility(View.VISIBLE);

        Media media = new Media(libVLC, Uri.parse(CAMERAS[camIndex]));
        mediaPlayer.setMedia(media);
        mediaPlayer.play();

        camIndex = (camIndex + 1) % CAMERAS.length;
        handler.postDelayed(this::showImage, CAMERA_TIME);
    }

    private void stopCamera() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
}
