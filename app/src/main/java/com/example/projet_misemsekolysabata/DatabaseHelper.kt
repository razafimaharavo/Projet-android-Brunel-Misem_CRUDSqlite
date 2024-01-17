package com.example.projet_misemsekolysabata

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "members.db"
        private const val DATABASE_VERSION = 1

        // Table et colonnes
        private const val TABLE_MEMBERS = "members"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_DATE_OF_BIRTH = "date_of_birth"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_MEMBERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_GENDER TEXT," +
                "$COLUMN_DATE_OF_BIRTH TEXT);"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEMBERS")
        onCreate(db)
    }

    // Méthode pour ajouter un membre à la base de données
    fun addMember(member: Member): Long {
        val values = ContentValues()
        values.put(COLUMN_NAME, member.name)
        values.put(COLUMN_GENDER, member.gender)
        values.put(COLUMN_DATE_OF_BIRTH, member.dateOfBirth)

        val db = writableDatabase
        val id = db.insert(TABLE_MEMBERS, null, values)
        db.close()

        return id
    }

    // Méthode pour récupérer tous les membres de la base de données
    fun getAllMembers(): List<Member> {
        val membersList = mutableListOf<Member>()
        val selectQuery = "SELECT * FROM $TABLE_MEMBERS"

        val db = readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLException) {
            db.execSQL(selectQuery)
            return emptyList()
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Vérifiez l'index de chaque colonne avant de l'utiliser
                val columnIndexId = cursor.getColumnIndex(COLUMN_ID)
                val columnIndexName = cursor.getColumnIndex(COLUMN_NAME)
                val columnIndexGender = cursor.getColumnIndex(COLUMN_GENDER)
                val columnIndexDateOfBirth = cursor.getColumnIndex(COLUMN_DATE_OF_BIRTH)

                // l'index est valide (≥ 0) avant d'extraire la valeur
                if (columnIndexId >= 0 && columnIndexName >= 0 && columnIndexGender >= 0 && columnIndexDateOfBirth >= 0) {
                    val member = Member(
                        cursor.getLong(columnIndexId),
                        cursor.getString(columnIndexName),
                        cursor.getString(columnIndexGender),
                        cursor.getString(columnIndexDateOfBirth)
                    )
                    membersList.add(member)
                }
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return membersList
    }

    // Méthode pour mettre à jour un membre dans la base de données
    fun updateMember(member: Member): Int {
        val values = ContentValues()
        values.put(COLUMN_NAME, member.name)
        values.put(COLUMN_GENDER, member.gender)
        values.put(COLUMN_DATE_OF_BIRTH, member.dateOfBirth)

        val db = writableDatabase
        val rowsAffected = db.update(
            TABLE_MEMBERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(member.id.toString())
        )

        db.close()
        return rowsAffected
    }

    // Méthode pour supprimer un membre de la base de données
    fun deleteMember(memberId: Long): Int {
        val db = writableDatabase
        val rowsAffected = db.delete(
            TABLE_MEMBERS,
            "$COLUMN_ID = ?",
            arrayOf(memberId.toString())
        )

        db.close()
        return rowsAffected
    }

    fun getMember(memberId: Long): Member? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MEMBERS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_GENDER, COLUMN_DATE_OF_BIRTH),
            "$COLUMN_ID = ?",
            arrayOf(memberId.toString()),
            null, null, null
        )

        val member: Member? = if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val genderIndex = cursor.getColumnIndex(COLUMN_GENDER)
            val dobIndex = cursor.getColumnIndex(COLUMN_DATE_OF_BIRTH)

            val name = if (nameIndex != -1) cursor.getString(nameIndex) else ""
            val gender = if (genderIndex != -1) cursor.getString(genderIndex) else ""
            val dob = if (dobIndex != -1) cursor.getString(dobIndex) else ""

            Member(memberId, name, gender, dob)
        } else {
            null
        }

        cursor.close()
        return member
    }


}
