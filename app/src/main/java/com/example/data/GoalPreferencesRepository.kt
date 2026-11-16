package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.hishhDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "hishh_money_prefs"
)

class GoalPreferencesRepository(private val context: Context) {
    private val monthlyGoalKey = doublePreferencesKey("monthly_savings_goal")

    val monthlyGoalFlow: Flow<Double> = context.hishhDataStore.data.map { prefs ->
        prefs[monthlyGoalKey] ?: DEFAULT_MONTHLY_GOAL
    }

    suspend fun updateMonthlyGoal(goal: Double) {
        val sanitized = goal.coerceAtLeast(0.0)
        context.hishhDataStore.edit { prefs ->
            prefs[monthlyGoalKey] = sanitized
        }
    }

    suspend fun resetMonthlyGoal() {
        context.hishhDataStore.edit { prefs ->
            prefs[monthlyGoalKey] = DEFAULT_MONTHLY_GOAL
        }
    }

    companion object {
        const val DEFAULT_MONTHLY_GOAL = 5000.0
    }
}
