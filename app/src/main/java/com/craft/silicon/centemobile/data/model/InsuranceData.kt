package com.craft.silicon.centemobile.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InsuranceData(
    @field:SerializedName("PolicyTerm")
    @field:Expose
    var policyTerm: String,
    @field:SerializedName("PremiumAmount")
    @field:Expose
    var premiumAmount: String,
    @field:SerializedName("SumAssured")
    @field:Expose
    var sumAssured: String,
) {
    override fun toString(): String {
        return policyTerm
    }
}