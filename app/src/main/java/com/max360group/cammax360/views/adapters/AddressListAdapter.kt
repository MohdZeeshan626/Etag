package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.OwnerAddressModel
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.GeneralInfoInterface
import kotlinx.android.synthetic.main.layout_address.view.*


class AddressListAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mAddressList = mutableListOf<OwnerAddressModel>()
    private var mDefaultAddresses=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_address))
    }

    override fun getItemCount(): Int {
        return mAddressList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    fun updateData(list: List<OwnerAddressModel>,defaultAddress:Int=2) {
        mAddressList.clear()
        mAddressList.addAll(list)
        mDefaultAddresses=defaultAddress
        notifyDataSetChanged()
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvAddressName.text = mAddressList[absoluteAdapterPosition].name
            itemView.tvAddress.text = mAddressList[absoluteAdapterPosition].formatted

            if (mAddressList[absoluteAdapterPosition].location.coordinates!!.isNotEmpty()){
                itemView.ivMap.setImageURI(GeneralFunctions.getStaticMap(mAddressList[absoluteAdapterPosition].location.coordinates!![0],
                    mAddressList[absoluteAdapterPosition].location.coordinates!![1]))
            }else{
                itemView.ivMap.setImageURI(GeneralFunctions.getStaticMap(0.0,
                    0.0))
            }

            itemView.ivDelete.setOnClickListener {
                (mFragment as GeneralInfoInterface).onDeleteAddress(absoluteAdapterPosition)
            }

            itemView.ivEdit.setOnClickListener {
                (mFragment as GeneralInfoInterface).onEditAddress(absoluteAdapterPosition,mAddressList[absoluteAdapterPosition])
            }

            if (mDefaultAddresses==2){
                if (absoluteAdapterPosition == 0 || absoluteAdapterPosition == 1) {
                    itemView.ivDelete.visibility = View.GONE
                } else {
                    itemView.ivDelete.visibility = View.VISIBLE
                }
            }else{
                if (absoluteAdapterPosition == 0) {
                    itemView.ivDelete.visibility = View.GONE
                } else {
                    itemView.ivDelete.visibility = View.VISIBLE
                }
            }


        }
    }
}