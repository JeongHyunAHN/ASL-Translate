package com.jeong.dearadam;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
// old ImageDisplayActivity

/*
cette classe effectue des requêtes Firebase
pour télécharger des images
et les afficher dans le bon ordre

la variable "id" sert comme une grille d'affichage
*/

public class Translate extends AppCompatActivity {

    FirebaseStorage storage;
    StorageReference storageRef;
    ImageView myImg;
    TextView textDisplay;
    File file;

    String textToDisplay;

    Set<Signe> signes;
    Bitmap myBitmap;
    Iterator<Signe> iSigne;

    int posX, posY; // les coordonnées pour la grille

    int[][] id = {{R.id.ASLDisplay00_imageDisplayActivity, R.id.ASLDisplay01_imageDisplayActivity, R.id.ASLDisplay02_imageDisplayActivity, R.id.ASLDisplay03_imageDisplayActivity, R.id.ASLDisplay04_imageDisplayActivity, R.id.ASLDisplay05_imageDisplayActivity, R.id.ASLDisplay06_imageDisplayActivity, R.id.ASLDisplay07_imageDisplayActivity},
            {R.id.ASLDisplay10_imageDisplayActivity, R.id.ASLDisplay11_imageDisplayActivity, R.id.ASLDisplay12_imageDisplayActivity, R.id.ASLDisplay13_imageDisplayActivity, R.id.ASLDisplay14_imageDisplayActivity, R.id.ASLDisplay15_imageDisplayActivity, R.id.ASLDisplay16_imageDisplayActivity, R.id.ASLDisplay17_imageDisplayActivity},
            {R.id.ASLDisplay20_imageDisplayActivity, R.id.ASLDisplay21_imageDisplayActivity, R.id.ASLDisplay22_imageDisplayActivity, R.id.ASLDisplay23_imageDisplayActivity, R.id.ASLDisplay24_imageDisplayActivity, R.id.ASLDisplay25_imageDisplayActivity, R.id.ASLDisplay26_imageDisplayActivity, R.id.ASLDisplay27_imageDisplayActivity},
            {R.id.ASLDisplay30_imageDisplayActivity, R.id.ASLDisplay31_imageDisplayActivity, R.id.ASLDisplay32_imageDisplayActivity, R.id.ASLDisplay33_imageDisplayActivity, R.id.ASLDisplay34_imageDisplayActivity, R.id.ASLDisplay35_imageDisplayActivity, R.id.ASLDisplay36_imageDisplayActivity, R.id.ASLDisplay37_imageDisplayActivity},
            {R.id.ASLDisplay40_imageDisplayActivity, R.id.ASLDisplay41_imageDisplayActivity, R.id.ASLDisplay42_imageDisplayActivity, R.id.ASLDisplay43_imageDisplayActivity, R.id.ASLDisplay44_imageDisplayActivity, R.id.ASLDisplay45_imageDisplayActivity, R.id.ASLDisplay46_imageDisplayActivity, R.id.ASLDisplay47_imageDisplayActivity},
            {R.id.ASLDisplay50_imageDisplayActivity, R.id.ASLDisplay51_imageDisplayActivity, R.id.ASLDisplay52_imageDisplayActivity, R.id.ASLDisplay53_imageDisplayActivity, R.id.ASLDisplay54_imageDisplayActivity, R.id.ASLDisplay55_imageDisplayActivity, R.id.ASLDisplay56_imageDisplayActivity, R.id.ASLDisplay57_imageDisplayActivity},
            {R.id.ASLDisplay60_imageDisplayActivity, R.id.ASLDisplay61_imageDisplayActivity, R.id.ASLDisplay62_imageDisplayActivity, R.id.ASLDisplay63_imageDisplayActivity, R.id.ASLDisplay64_imageDisplayActivity, R.id.ASLDisplay65_imageDisplayActivity, R.id.ASLDisplay66_imageDisplayActivity, R.id.ASLDisplay67_imageDisplayActivity},
            {R.id.ASLDisplay70_imageDisplayActivity, R.id.ASLDisplay71_imageDisplayActivity, R.id.ASLDisplay72_imageDisplayActivity, R.id.ASLDisplay73_imageDisplayActivity, R.id.ASLDisplay74_imageDisplayActivity, R.id.ASLDisplay75_imageDisplayActivity, R.id.ASLDisplay76_imageDisplayActivity, R.id.ASLDisplay77_imageDisplayActivity}};


    void translationDisplay(){ // cette fonction permet de placer les images dans les bons endroits
        posX = 0;
        posY = 0;
        Bundle extras = getIntent().getExtras();
        textToDisplay = extras.getString("Data").toLowerCase();
        textDisplay.setText(textToDisplay);

        signes = new HashSet<Signe>(); // objet Set de signes créé
        iSigne = signes.iterator(); // iterator créé pour cette phrase à traduire

        for (int i = 0; i < textToDisplay.length(); i++) {
            char instance = textToDisplay.charAt(i);
            if (Character.isAlphabetic(instance)) {
                displayAlphaImage(instance,posX,posY);
            } else if (Character.isDigit(instance)) {
                displayDigitImage(instance,posX,posY);
            } else if (Character.isSpaceChar(instance)) {
                posX++;
                posY = -1;
            }
            if (posY < 7) {
                posY++;
            } else { //si le mot à afficher est trop long, saut de ligne
                posX++;
                posY = 0;
            }
            if (posX == 8){
                Toast.makeText(getApplicationContext(), "Word limit exceeded", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        storage = FirebaseStorage.getInstance();
        textDisplay = findViewById(R.id.convertedTextDisplay_imageDisplayActivity);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1F00AB"));
        actionBar.setBackgroundDrawable(colorDrawable);

        translationDisplay();
    }

    // cette methode telecharge les images des chiffres dans les BD
    private void displayDigitImage(char digit, int x, int y) {
        myBitmap = null;
        iSigne = signes.iterator();
        while (iSigne.hasNext()) { // parcours dans Set pour voir si le mot est dedans
            Signe signe = iSigne.next(); // un nouveau objet Signe crée, il
            if (digit == signe.getName()){ // si existe déjà dans Set
                myBitmap = signe.getImageToShow(); // recopie l'image
                myImg = findViewById(id[x][y]); // et l'affiche
                myImg.setImageBitmap(myBitmap);
            }
        }
        if (myBitmap == null) { // si n'existe pas dans Set
            String childPath = "alphabetImg/" + digit + ".gif"; // faut aller chercher url dans Firebase
            storageRef = storage.getReferenceFromUrl("gs://hluchy-a4293.appspot.com").child(childPath);
            try {
                file = File.createTempFile("image", "gif");
                File finalFile = file;
                storageRef.getFile(file).addOnSuccessListener(taskSnapshot -> {
                    myBitmap = BitmapFactory.decodeFile(finalFile.getAbsolutePath()); // avant myBitmap null
                    Signe sig = new Signe(digit,myBitmap); // création Signe avec bitmap obtenu pour ajouter dans set
                    signes.add(sig); // new Signe ajouté dans Set
                    myImg = findViewById(id[x][y]); // affichage
                    myImg.setImageBitmap(myBitmap);
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Image Failed to Load", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // cette fonction telecharge les images des alphabets dans les BD
    private void displayAlphaImage(char letter, int x, int y) {
        myBitmap = null;
        iSigne = signes.iterator();
        while (iSigne.hasNext()) {
            Signe signe = iSigne.next();
            if (letter == signe.getName()){
                myBitmap = signe.getImageToShow(); // si existe déjà dans Set, myBitmap copie
                myImg = findViewById(id[x][y]);
                myImg.setImageBitmap(myBitmap);
            }
        }
        if (myBitmap == null) { // si n'existe pas dans Set
            String childPath = "alphabetImg/" + letter + ".gif";
            storageRef = storage.getReferenceFromUrl("gs://hluchy-a4293.appspot.com").child(childPath);

            try {
                file = File.createTempFile("image", "gif");
                File finalFile = file;
                storageRef.getFile(file).addOnSuccessListener(taskSnapshot -> { // lambda expression
                    myBitmap = BitmapFactory.decodeFile(finalFile.getAbsolutePath());
                    Signe sig = new Signe(letter, myBitmap);
                    signes.add(sig); // ajout dans Set
                    myImg = findViewById(id[x][y]); // affichage
                    myImg.setImageBitmap(myBitmap);
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Image Failed to Load", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}