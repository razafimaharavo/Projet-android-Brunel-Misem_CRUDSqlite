package com.example.projet_misemsekolysabata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var fabAddMember: FloatingActionButton
    // private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewMembers)
        databaseHelper = DatabaseHelper(this)
        fabAddMember = findViewById(R.id.floatingActionButton2)
        fabAddMember.setImageResource(R.drawable.add_male_user_group_100px)


        // Obtenez tous les membres de la base de données
        val membersList = databaseHelper.getAllMembers()

        // Initialisez et attachez l'adaptateur au RecyclerView
        memberAdapter = MemberAdapter(this, membersList)
        recyclerView.adapter = memberAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fabAddMember.setOnClickListener {
            val intent = Intent(this, MemberMangment::class.java)
            startActivityForResult(intent, MemberMangment.ADD_EDIT_MEMBER_REQUEST_CODE)
        }


    }

    // Ajoutez cette fonction pour gérer le résultat après l'édition/ajout
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MemberMangment.ADD_EDIT_MEMBER_REQUEST_CODE && resultCode == RESULT_OK) {
            // Mise à jour de la liste après l'édition/ajout
            memberAdapter.updateMembers(databaseHelper.getAllMembers())
        }
    }

}