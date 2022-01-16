package com.jeong.dearadam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Locale;
// old AudioInputActivity

/*
    cette classe écoute l'user
    et convertit en String ce que l'user dit
    puis l'envoi à la classe Translate

    un objet de type Intent
 */

public class Listen extends AppCompatActivity {

    ImageButton mic;
    Button textButton;
    EditText textInput;

    String text;
    Intent intent;

    void envoiPourTrad(Intent data){
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); // stocker le texte ici
        intent = new Intent(getApplicationContext(), Translate.class); // objet Intent c lui qui va convertir la parole
        intent.putExtra("Data",result.get(0));
        startActivity(intent);
    }

    void convertParole(){
        mic.setOnClickListener(v -> {
            Intent intentSpeechToText = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intentSpeechToText.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intentSpeechToText.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            if(intentSpeechToText.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intentSpeechToText, 10);
            } else {
                Toast.makeText(getApplicationContext(), "Your Device doesn't support Speech Input", Toast.LENGTH_SHORT).show();
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = textInput.getText().toString();
                if(text.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter some text", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Translate.class);
                    intent.putExtra("Data",text);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_input);
        mic = findViewById(R.id.button_mic_audioInputActivity);
        textButton = findViewById(R.id.goThroughText_audioInputActivity);
        textInput = findViewById(R.id.textInput_audioInputActivity);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1F00AB"));
        actionBar.setBackgroundDrawable(colorDrawable);

        convertParole();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                envoiPourTrad(data);
            }
        }
    }
}
