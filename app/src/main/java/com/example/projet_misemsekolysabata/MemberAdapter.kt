package com.example.projet_misemsekolysabata

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.text.ParseException


class MemberAdapter(private val context: Context, private var members: List<Member>) :
    RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val detailsTextView: TextView = itemView.findViewById(R.id.textViewDetails)
        val editButton: Button = itemView.findViewById(R.id.buttonEdit)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDelete)
    }


private fun calculateAge(dateOfBirth: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    try {
        val birthDate = Calendar.getInstance()
        birthDate.time = sdf.parse(dateOfBirth)

        val currentDate = Calendar.getInstance()
        val age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        // Vérifie si l'anniversaire n'est pas encore arrivé cette année
        if (currentDate.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            return (age - 1).toString()
        }

        return age.toString()
    } catch (e: ParseException) {
        // Gérer l'erreur de format de date ici (afficher un message ou renvoyer une valeur par défaut)
        e.printStackTrace()
        return "Erreur de format de date"
    }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        // Obtient le service de création de vues à partir du contexte
        val inflater = LayoutInflater.from(context)
        // Inflates (crée) une vue à partir du fichier XML 'item_member'
        val view = inflater.inflate(R.layout.item_member, parent, false)
        // Retourne une nouvelle instance du ViewHolder avec la vue créée
        return MemberViewHolder(view)
    }


    // Fonction pour lier les données à une vue ViewHolder
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]
        val age = calculateAge(member.dateOfBirth)
        holder.nameTextView.text = member.name
        holder.detailsTextView.text = "$age - ${member.gender}"

        holder.editButton.setOnClickListener {
            // Crée une intention pour l'activité de gestion des membres avec l'ID du membre en extra
            val editIntent = Intent(context, MemberMangment::class.java)
            editIntent.putExtra(MemberMangment.EXTRA_MEMBER_ID, member.id)

            // Vérifie si le contexte est une instance d'Activity avant de démarrer l'activité
            if (context is Activity) {
                // Démarre l'activité d'édition avec la demande de code spécifiée powered by Brunel
                context.startActivityForResult(
                    editIntent,
                    MemberMangment.ADD_EDIT_MEMBER_REQUEST_CODE
                )
            }else {
                // Gérer le cas où le contexte n'est pas une instance d'Activity
                // Afficher un message d'erreur à l'utilisateur
                AlertDialog.Builder(context)
                    .setTitle("Erreur")
                    .setMessage("Impossible de démarrer l'activité d'édition.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }


        holder.deleteButton.setOnClickListener {
            // Appeler la méthode de suppression dans la base de données
            showDeleteConfirmationDialog(member);
        }
    }

    private fun showDeleteConfirmationDialog(member: Member) {
        AlertDialog.Builder(context)
            .setTitle("Suppression du membre")
            .setMessage("Êtes-vous sûr de vouloir supprimer "+member.name+" dans la liste des membres")
            .setPositiveButton("Oui") { _, _ ->
                // Appeler la méthode de suppression dans la base de données
                val dbHelper = DatabaseHelper(context)
                dbHelper.deleteMember(member.id)

                // Mise à jour de la liste après la suppression
                updateMembers(dbHelper.getAllMembers())
            }
            .setNegativeButton("Non", null)
            .show()
    }

    // Ajoutez cette fonction pour mettre à jour la liste des membres
    fun updateMembers(newMembers: List<Member>) {
        members = newMembers
        notifyDataSetChanged()
    }
    // Fonction pour obtenir le nombre d'éléments dans la liste
    override fun getItemCount(): Int {
        return members.size
    }
}
