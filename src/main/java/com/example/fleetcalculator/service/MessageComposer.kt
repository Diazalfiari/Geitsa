package com.example.fleetcalculator.service

import com.example.fleetcalculator.data.model.LoggedInUser
import java.text.SimpleDateFormat
import java.util.*

/**
 * Service to compose formatted messages for Telegram
 */
class MessageComposer {

    companion object {
        private fun getCurrentTimestamp(): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(Date())
        }
    }

    /**
     * Compose a message for Hauler calculation
     */
    fun composeHaulerMessage(
        user: LoggedInUser,
        vesselCapacity: Double,
        jarak: Double,
        kecepatan: Double,
        travelTime: Double,
        cycleTime: Double,
        swellFactor: Double,
        efisiensiKerja: Double,
        productivity: Double
    ): String {
        return """
            📊 <b>Hauler Calculation Result</b>
            
            👤 <b>User:</b> ${user.displayName}
            📅 <b>Date:</b> ${getCurrentTimestamp()}
            
            <b>Input Parameters:</b>
            • Vessel Capacity: $vesselCapacity m³
            • Distance: $jarak m
            • Speed: $kecepatan km/h
            • Swell Factor: $swellFactor
            • Work Efficiency: $efisiensiKerja
            
            <b>Calculation Results:</b>
            • Travel Time (Round Trip): ${"%.2f".format(travelTime)} minutes
            • Cycle Time: ${"%.2f".format(cycleTime)} minutes
            • Productivity: ${"%.2f".format(productivity)} m³/hour
            
            🚛 Calculated via Geitsa Fleet Calculator
        """.trimIndent()
    }

    /**
     * Compose a message for Loader calculation
     */
    fun composeLoaderMessage(
        user: LoggedInUser,
        fleet: String,
        unit: String,
        bucketCapacity: Double,
        bucketFactor: Double,
        cycleTime: Double,
        efficiency: Double,
        productivity: Double
    ): String {
        return """
            📊 <b>Loader Calculation Result</b>
            
            👤 <b>User:</b> ${user.displayName}
            📅 <b>Date:</b> ${getCurrentTimestamp()}
            
            <b>Input Parameters:</b>
            • Fleet: $fleet
            • Unit: $unit
            • Bucket Capacity: $bucketCapacity m³
            • Bucket Factor: $bucketFactor
            • Cycle Time: $cycleTime minutes
            • Efficiency: $efficiency
            
            <b>Calculation Results:</b>
            • Productivity: ${"%.2f".format(productivity)} m³/hour
            
            🏗️ Calculated via Geitsa Fleet Calculator
        """.trimIndent()
    }

    /**
     * Compose a message for Jumlah Hauler calculation
     */
    fun composeJumlahHaulerMessage(
        user: LoggedInUser,
        calculationType: String,
        result: Double,
        additionalInfo: String = ""
    ): String {
        return """
            📊 <b>Jumlah Hauler Calculation Result</b>
            
            👤 <b>User:</b> ${user.displayName}
            📅 <b>Date:</b> ${getCurrentTimestamp()}
            
            <b>Calculation Type:</b> $calculationType
            
            <b>Result:</b> ${"%.2f".format(result)}
            
            ${if (additionalInfo.isNotEmpty()) "<b>Additional Info:</b>\n$additionalInfo\n" else ""}
            🚛 Calculated via Geitsa Fleet Calculator
        """.trimIndent()
    }

    /**
     * Compose a message for Matching Factor calculation
     */
    fun composeMatchingFactorMessage(
        user: LoggedInUser,
        calculationType: String,
        result: Double,
        additionalInfo: String = ""
    ): String {
        return """
            📊 <b>Matching Factor Calculation Result</b>
            
            👤 <b>User:</b> ${user.displayName}
            📅 <b>Date:</b> ${getCurrentTimestamp()}
            
            <b>Calculation Type:</b> $calculationType
            
            <b>Result:</b> ${"%.2f".format(result)}
            
            ${if (additionalInfo.isNotEmpty()) "<b>Additional Info:</b>\n$additionalInfo\n" else ""}
            ⚖️ Calculated via Geitsa Fleet Calculator
        """.trimIndent()
    }

    /**
     * Compose a generic calculation message
     */
    fun composeGenericMessage(
        user: LoggedInUser,
        calculationType: String,
        result: String,
        additionalInfo: String = ""
    ): String {
        return """
            📊 <b>$calculationType Calculation Result</b>
            
            👤 <b>User:</b> ${user.displayName}
            📅 <b>Date:</b> ${getCurrentTimestamp()}
            
            <b>Result:</b>
            $result
            
            ${if (additionalInfo.isNotEmpty()) "<b>Additional Info:</b>\n$additionalInfo\n" else ""}
            🧮 Calculated via Geitsa Fleet Calculator
        """.trimIndent()
    }
}