package com.max360group.cammax360.views.adapters

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Account
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_organisation_layout.view.*
import kotlinx.android.synthetic.main.toolbar.*


class OrganisationAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val mUserPrefsManager: UserPrefsManager by lazy { UserPrefsManager(mFragment.requireContext()) }
    private var mList = mutableListOf<Account>()
    private var mPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_organisation_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    fun updateData(accounts: List<Account>?) {
        mList.clear()
        mList.addAll(accounts!!)
        notifyDataSetChanged()

    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {

            //When account already switched
            if (mList[absoluteAdapterPosition].primaryUserId!!.id==mUserPrefsManager.getAccount){
                itemView.tvName.setTextColor(
                    MaterialColors.getColor(
                        mFragment.requireContext(),
                        R.attr.primaryTextColor,
                        Color.BLACK
                    )
                )
                itemView.ivCheck.visibility = View.VISIBLE

            }else{
                itemView.tvName.setTextColor(ContextCompat.getColor(mFragment.requireContext(),R.color.black))
                itemView.ivCheck.visibility = View.GONE
            }

            itemView.setOnClickListener {
                (mFragment as OrganisationListener).switchAccount(mList[absoluteAdapterPosition].primaryUserId!!.id.toString())
                notifyDataSetChanged()
            }

            itemView.tvName.text = mList[absoluteAdapterPosition].primaryUserId!!.account!!.name
            Glide
                .with(mFragment.requireContext())
                .load(mList[absoluteAdapterPosition].primaryUserId!!.account!!.logoURL)
                .placeholder(R.drawable.ic_mimi_logo)
                .into(itemView.ivLogo)
        }
    }

    interface OrganisationListener{
        fun switchAccount(id:String)
    }
}