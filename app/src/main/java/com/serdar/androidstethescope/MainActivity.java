package com.serdar.androidstethescope;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Timer.OnTimerTickListener {
    static int REQUEST_CODE = 200;
    String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
    boolean permissionGranted = false;

    Button cancelButton;
    Button okButton;
    ImageButton buttonRecord;
    ImageButton buttonDone;
    ImageButton buttonDelete;
    TextInputEditText fileNameInput;
    TextView textViewTimer;
    MediaRecorder recorder;
    WaveView waveView;

    String filePath;
    String fileName;

    SimpleDateFormat dateFormat;

    boolean isRecording = false;
    boolean isPaused = false;

    Timer timer;
    private ArrayList<Float> amplitudes = new ArrayList<>();

    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    View bottomSheetBackGround;
    LinearLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED;
        cancelButton = findViewById(R.id.buttoncancel);
        okButton = findViewById(R.id.buttonok);
        buttonRecord = findViewById(R.id.buttonrecord);
        buttonDone = findViewById(R.id.buttondone);
        buttonDelete = findViewById(R.id.buttondelete);
        textViewTimer = findViewById(R.id.textviewtimer);
        waveView = findViewById(R.id.waveview);
        fileNameInput = findViewById(R.id.filename);
        bottomSheetLayout = findViewById(R.id.bottomsheet);
        bottomSheetBackGround = findViewById(R.id.bottomsheetbg);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }

        timer = new Timer(this);

        buttonRecord.setOnClickListener(v -> {
            if (isPaused) resumeRecording();
            else if (isRecording) pauseRecording();
            else startRecording();
        });

        buttonDone.setOnClickListener(v -> {
            stopRecorder();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBackGround.setVisibility(View.VISIBLE);
            fileNameInput.setText(fileName);
        });

        buttonDelete.setOnClickListener(v -> {
            stopRecorder();
            new File(filePath + fileName + ".mp3").delete();
        });

        buttonDelete.setClickable(false);

        cancelButton.setOnClickListener(v -> {
            new File(filePath + fileName + ".mp3").delete();
            dismiss();
        });

        okButton.setOnClickListener(v -> {
            dismiss();
            save();
        });

        bottomSheetBackGround.setOnClickListener(v -> {
            new File(filePath + fileName + ".mp3").delete();
            dismiss();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void startRecording() {
        if (!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

        recorder = new MediaRecorder();
        filePath = getFilesDir().getAbsolutePath() + "/";
        dateFormat = new SimpleDateFormat("yyyy.mm.dd_hh.mm.ss");
        String date = dateFormat.format(new Date());
        fileName = "Audio_" + date;

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(filePath + fileName + ".mp3");

        try {
            recorder.prepare();
        } catch (Exception e) {

        }

        recorder.start();

        buttonRecord.setImageResource(R.drawable.ic_pause);
        isRecording = true;
        isPaused = false;

        timer.start();

        buttonDelete.setClickable(true);
        buttonDelete.setImageResource(R.drawable.ic_delete);

        buttonDone.setVisibility(View.VISIBLE);
    }

    private void pauseRecording() {
        recorder.pause();
        isPaused = true;
        buttonRecord.setImageResource(R.drawable.ic_record);
        timer.pause();
    }

    private void resumeRecording() {
        recorder.resume();
        isPaused = false;
        buttonRecord.setImageResource(R.drawable.ic_pause);

        timer.start();
    }

    private void stopRecorder() {
        timer.stop();
        recorder.stop();
        recorder.release();
        isPaused = false;
        isRecording = false;
        buttonDone.setVisibility(View.GONE);
        buttonDelete.setClickable(false);
        buttonDelete.setImageResource(R.drawable.ic_delete_disabled);
        buttonRecord.setImageResource(R.drawable.ic_record);
        textViewTimer.setText("00.00.00");
        amplitudes = waveView.clear();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void dismiss() {
        bottomSheetBackGround.setVisibility(View.GONE);
        hideKeyboard(fileNameInput);
        new Handler(Looper.getMainLooper()).postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED), 100);
    }

    private void save() {
        String newFileName = fileNameInput.getText().toString();

        if (!newFileName.equals(fileName)) {
            File newFile = new File(filePath + newFileName + ".mp3");
            new File(filePath + fileName + ".mp3").renameTo(newFile);
        }
    }

    @Override
    public void onTimerTick(String duration) {
        textViewTimer.setText(duration);
        waveView.addAmplitude((float) recorder.getMaxAmplitude());
    }
}