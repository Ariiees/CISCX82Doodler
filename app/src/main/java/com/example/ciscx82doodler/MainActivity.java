package com.example.ciscx82doodler;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private DooleView drawingCanvas;
    private SeekBar seekBarSize, seekBarOpacity;
    private Button colorPicker, clearButton, saveButton;
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

        // Set brush size from SeekBar
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

        // Set brush opacity from SeekBar
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

        colorPicker.setOnClickListener(v -> {
            // Open color picker dialog
            ColorPickerDialogBuilder
                    .with(MainActivity.this)
                    .setTitle("Choose color")
                    .initialColor(brushColor)  // Set the initial color to the brushColor
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)  // Set the wheel type to "Flower"
                    .density(12)  // Set the density of the color wheel
                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int selectedColor) {
                            Toast.makeText(MainActivity.this, "Selected color: " + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setPositiveButton("ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            brushColor = selectedColor;
                            drawingCanvas.setBrushColor(brushColor);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .build()
                    .show();  // Show the color picker dialog
        });


        // Clear drawing canvas
        clearButton.setOnClickListener(v -> drawingCanvas.clearCanvas());

        // Save drawing to file
        saveButton.setOnClickListener(v -> {
            Bitmap bitmap = drawingCanvas.getBitmap();
            saveDrawingToFile(bitmap);
        });
    }

    private void saveDrawingToFile(Bitmap bitmap) {
        // Get the filename for the image
        String fileName = "drawing_" + System.currentTimeMillis() + ".png";

        // Create a file output stream in the app's internal storage
        try (OutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            // Compress the bitmap into the file as a PNG
            boolean isSaved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            if (isSaved) {
                // Show a success message to the user
                Toast.makeText(this, "Drawing saved to " + fileName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save drawing", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving the drawing", Toast.LENGTH_SHORT).show();
        }
    }
}