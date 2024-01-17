package com.example.projet_misemsekolysabata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import java.text.SimpleDateFormat
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.widget.ImageView
import java.text.ParseException
import com.google.android.material.snackbar.Snackbar

class MemberMangment : AppCompatActivity() {

    companion object {
        const val ADD_EDIT_MEMBER_REQUEST_CODE = 1001
        const val EXTRA_MEMBER_ID = "extra_member_id"
    }

    private lateinit var nameEditText: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var dobEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var dbHelper: DatabaseHelper
    private var memberId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_mangment)

        nameEditText = findViewById(R.id.editTextName)
        genderSpinner = findViewById(R.id.spinnerGender)
        dobEditText = findViewById(R.id.editTextDate)
        saveButton = findViewById(R.id.buttonSave)

        val imageView = findViewById<ImageView>(R.id.imageView)

        // Définir l'opacité à 0.5 (50% d'opacité)
        imageView.alpha = 0.7f


        dbHelper = DatabaseHelper(this)

        // Créer une liste d'options pour le Spinner (peut-être dans les ressources strings.xml)
        val genderOptions = resources.getStringArray(R.array.gender_options)

        // Crée un ArrayAdapter avec le contexte actuel, une mise en page simple pour les éléments du Spinner (android.R.layout.simple_spinner_item),
        // et la liste des options de genre (genderOptions)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)

        // Spécifie la mise en page à utiliser pour la liste déroulante du Spinner (une mise en page simple avec un seul choix par élément)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        // Appliquer l'adaptateur au Spinner
        genderSpinner.adapter = adapter


        if (intent.hasExtra(EXTRA_MEMBER_ID)) {
            // Si l'intent contient l'extra EXTRA_MEMBER_ID
            memberId = intent.getLongExtra(EXTRA_MEMBER_ID, -1)
            // Récupère l'ID du membre à partir de l'intent, avec une valeur par défaut de -1 si l'extra n'est pas présent

            // Vérifie si l'ID du membre est valide (différent de -1)
            if (memberId != -1L) {
                // Récupère le membre à partir de la base de données en utilisant l'ID
                val member = dbHelper.getMember(memberId)
                // Vérifie si le membre est non nul
                if (member != null) {
                    // Remplit le champ de texte avec le nom du membre
                    nameEditText.setText(member.name)
                    // Utiliser setSelection pour sélectionner l'élément correct dans le Spinner
                    genderSpinner.setSelection(genderOptions.indexOf(member.gender))
                    // Sélectionne le genre du membre dans le Spinner en utilisant la position de l'élément dans la liste des options

                    dobEditText.setText(member.dateOfBirth)
                    // Remplit le champ de texte avec la date de naissance du membre
                }
            }
        }


        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val gender = genderSpinner.selectedItem.toString()
            val dob = dobEditText.text.toString()

            if (isValidDate(dob)) {
            if (name.isNotEmpty() && gender.isNotEmpty() && dob.isNotEmpty()) {
                val newMember = Member(memberId, name, gender, dob)

                if (memberId == -1L) {
                    dbHelper.addMember(newMember)
                } else {
                    dbHelper.updateMember(newMember)
                }

                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Veuillez remplir tous les champs.",
                    Toast.LENGTH_LONG
                ).show()
            }

            } else {
                Toast.makeText(
                    this,
                    "Format de date incorrect. Utilisez le format YYYY-MM-DD.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun isValidDate(date: String): Boolean {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            sdf.isLenient = false
            sdf.parse(date)
            return true
        } catch (e: ParseException) {
            return false
        }
    }
}