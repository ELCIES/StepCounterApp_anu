package com.sd.src.stepcounterapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sd.src.stepcounterapp.R
import com.sd.src.stepcounterapp.fragments.WalletFragment
import com.sd.src.stepcounterapp.model.wallet.walletDetailResponse.Purchased
import com.sd.src.stepcounterapp.network.RetrofitClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_wallet_reedem_list.view.*

class WalletRedeemListAdapter(var mData: ArrayList<Purchased>) :
    RecyclerView.Adapter<WalletRedeemListAdapter.ViewHolder>() {

    override
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_wallet_reedem_list, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtProductName.text = mData[position].name
        holder.txtShortDesc.text = mData[position].shortDesc
        holder.txtToken.text = "${mData[position].token} TKS"
        Picasso.get().load(RetrofitClient.IMG_URL + "" + mData[position].image).into(holder.imgProduct)
        holder.redeemedParent.setOnClickListener {
                WalletFragment.instance.onRedeem(position)
        }

    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct = itemView.imgProduct!!
        val txtProductName = itemView.txtProductName!!
        val txtShortDesc = itemView.txtShortDesc!!
        val txtToken = itemView.txtToken!!
        val redeemedParent = itemView.redeemedParent!!
    }

    interface RedeemListener{
        fun onRedeem(pos: Int)
    }
}