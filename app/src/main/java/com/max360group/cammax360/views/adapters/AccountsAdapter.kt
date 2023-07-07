package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Account
import com.max360group.cammax360.repository.models.AccountBitValues
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.fragment_accounts_detail.*
import kotlinx.android.synthetic.main.load_accounts_layout.view.*
import java.io.File


class AccountsAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<Account>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_accounts_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<Account>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {

            var mAccountBitValues= AccountBitValues()
            if (mList[adapterPosition].userType==mAccountBitValues.basic){
              itemView.tvType.text=mFragment.requireContext().getString(R.string.st_basic)
            }

            if (mList[adapterPosition].userType==mAccountBitValues.superAdmin){
                itemView.tvType.text=mFragment.requireContext().getString(R.string.st_super_admin)
            }

            if (mList[adapterPosition].userType==mAccountBitValues.admin){
                itemView.tvType.text=mFragment.requireContext().getString(R.string.st_admin)
            }

            if (mList[adapterPosition].userType==mAccountBitValues.owner){
                itemView.tvType.text=mFragment.requireContext().getString(R.string.st_owner)
            }

            if (mList[adapterPosition].userType==mAccountBitValues.user){
                itemView.tvType.text=mFragment.requireContext().getString(R.string.st_user)
            }

            if (mList[adapterPosition].primaryUserId!!.account!!.logoURL!!.startsWith("https://")){
                itemView.ivLogo.setImageURI(mList[adapterPosition].primaryUserId!!.account!!.logoURL)
            }else{
                itemView.ivLogo.setImageURI(GeneralFunctions.getLocalImageFile(File(mList[adapterPosition].primaryUserId!!.account!!.logoURL!!)))
            }

            itemView.tvName.text=mList[adapterPosition].primaryUserId!!.account!!.name
            itemView.tvEmail.text=mList[adapterPosition].primaryUserId!!.account!!.primaryEmail
            itemView.tvPhoneNumber.text=mList[adapterPosition].primaryUserId!!.account!!.phone

            if (adapterPosition == 0) {
                itemView.btnViewDetail.visibility = View.VISIBLE

            } else {
                itemView.btnViewDetail.visibility = View.GONE
            }
            itemView.btnViewDetail.setOnClickListener {
                (mFragment as AccountListener).onItemClick(mList[adapterPosition])
            }
        }
    }

    interface AccountListener {
        fun onItemClick(account: Account)
    }
}