package com.example.ciscx82doodler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private DooleView drawingCanvas;
    private SeekBar seekBarSize, seekBarOpacity;
    private Button colorPicker, clearButton, saveButton, loadButton, shareButton;
    private String lastSavedFileName = null; // Initialized to null
//    private Bitmap savedBitemap = null;
    private int brushSize = 10;
    private int brushColor = Color.BLACK;
    private int brushOpacity = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingCanvas = findViewById(R.id.drawingCanvas);
        seekBarSize = findViewById(R.id.seekBarSize);
        seekBarOpacity = findViewById(R.id.seekBarOpacity);
        colorPicker = findViewById(R.id.colorPicker);
        clearButton = findViewById(R.id.clearButton);
        saveButton = findViewById(R.id.saveButton);
        loadButton = findViewById(R.id.loadButton);
        shareButton = findViewById(R.id.shareButton);

        // Brush size change listener
        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushSize = progress;
                drawingCanvas.setBrushSize(brushSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Brush opacity change listener
        seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushOpacity = progress;
                drawingCanvas.setBrushOpacity(brushOpacity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Color picker dialog
        colorPicker.setOnClickListener(v -> ColorPickerDialogBuilder
                .with(MainActivity.this)
                .setTitle("Choose color")
                .initialColor(brushColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor ->
                        Toast.makeText(MainActivity.this, "Selected color: " + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show())
                .setPositiveButton("OK", (dialog, selectedColor, allColors) -> {
                    brushColor = selectedColor;
                    drawingCanvas.setBrushColor(brushColor);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .build()
                .show());

        // Clear canvas
        clearButton.setOnClickListener(v -> drawingCanvas.clearCanvas());

        // Save button functionality
        saveButton.setOnClickListener(v -> {
            Bitmap bitmap = drawingCanvas.getBitmap();
            saveDrawingToFile(bitmap);
        });

        // Load button functionality
        loadButton.setOnClickListener(v -> {
            if (lastSavedFileName != null) {
                loadImageFromFile(lastSavedFileName);
            } else {
                Toast.makeText(this, "No saved drawing to load", Toast.LENGTH_SHORT).show();
            }
        });

        // Social media share functionality
        shareButton.setOnClickListener(v -> {
            if (lastSavedFileName != null) {
                shareDrawing(lastSavedFileName);
            } else {
                Toast.makeText(this, "No saved drawing to share", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDrawingToFile(Bitmap bitmap) {
        String fileName = "drawing_" + System.currentTimeMillis() + ".png";
        try (OutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            boolean isSaved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            if (isSaved) {
                lastSavedFileName = fileName;
                Toast.makeText(this, "Drawing saved as " + fileName, Toast.LENGTH_SHORT).show();
            } else {
                Log.e("MainActivity", "Failed to save drawing: Bitmap compression failed.");
                Toast.makeText(this, "Failed to save drawing", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Error saving the drawing: " + e.getMessage(), e);
            Toast.makeText(this, "Error saving the drawing", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImageFromFile(String fileName) {
        try (InputStream inputStream = openFileInput(fileName)) {
            Bitmap loadedBitmap = BitmapFactory.decodeStream(inputStream);

            if (loadedBitmap != null) {
                drawingCanvas.setBitmap(loadedBitmap); // Set bitmap to canvas
                Toast.makeText(this, "Loaded drawing: " + fileName, Toast.LENGTH_SHORT).show();
            } else {
                Log.e("MainActivity", "Failed to load drawing: File corrupted or invalid bitmap.");
                Toast.makeText(this, "Failed to load drawing: Invalid file.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Error loading drawing: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to load drawing", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareDrawing(String fileName) {
        try (InputStream inputStream = openFileInput(fileName)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap != null) {
                // Save the bitmap to a temporary file to share it
                File cachePath = new File(getCacheDir(), "images");
                cachePath.mkdirs();
                File tempFile = new File(cachePath, "shared_drawing.png");

                try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();

                    Uri imageUri = FileProvider.getUriForFile(this,
                            "com.example.ciscx82doodler.fileprovider", tempFile);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/png");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out my drawing!");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "I made this awesome doodle! Check it out.");
                    startActivity(Intent.createChooser(shareIntent, "Share drawing"));
                } catch (IOException e) {
                    Log.e("MainActivity", "Error sharing the drawing: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to share drawing", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading saved file for sharing: " + e.getMessage(), e);
            Toast.makeText(this, "Error accessing saved drawing", Toast.LENGTH_SHORT).show();
        }
    }

}
