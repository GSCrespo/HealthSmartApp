package br.edu.ifsp.dmo2.healthsmartapp.model

data class Workout (
    val userId: String = "",
    val steps: Int = 0,
    val durationMinutes: Double = 0.0,
    val estimatedCalories: Double = 0.0,
    val timestamp: Long = 0L)
{

}