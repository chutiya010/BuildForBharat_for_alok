package com.example.buildforbharat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MerchantAdapter() :
    RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder>() {
    private val merchantList: MutableList<Merchant> = mutableListOf()
    fun addMerchant(merchant: Merchant) {
        merchantList.add(merchant)
        notifyItemInserted(merchantList.size - 1)
    }

    class MerchantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val merchantName: TextView = itemView.findViewById(R.id.textMerchantName)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_merchant, parent, false)
        return MerchantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        val currentMerchant = merchantList[position]
        holder.merchantName.text = currentMerchant.pincode
    }

    override fun getItemCount(): Int {
        return merchantList.size
    }
}
