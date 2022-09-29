package com.elmacentemobile.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoanData(
    @field:SerializedName("LoanAccount")
    @field:Expose
    var loanAccount: String,
    @field:SerializedName("LoanAccountCode")
    @field:Expose
    var loanAccountCode: String,
    @field:SerializedName("ProductId")
    @field:Expose
    var productId: String,

    @field:SerializedName("InstallmentAmount")
    @field:Expose
    var installmentAmount: String,

    @field:SerializedName("TotalAmount")
    @field:Expose
    var totalAmount: String,

    @field:SerializedName("Description")
    @field:Expose
    var description: String,
) {
    override fun toString(): String {
        return description
    }
}
