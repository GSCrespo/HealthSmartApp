package br.edu.ifsp.dmo2.healthsmartapp.firebase

import br.edu.ifsp.dmo2.healthsmartapp.model.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


object database {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveWorkout(
        workout: Workout,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        val workoutData = workout.copy(userId = userId)

        db.collection("workouts")
            .add(workoutData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun loadUserWorkouts(
        onResult: (List<Workout>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("workouts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val workouts = result.mapNotNull { it.toObject(Workout::class.java) }
                onResult(workouts)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}