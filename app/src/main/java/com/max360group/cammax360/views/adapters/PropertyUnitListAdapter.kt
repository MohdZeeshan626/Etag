package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.UnitRecord
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.layout_property_unit.view.*

open class PropertyUnitListAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<UnitRecord>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_property_unit))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateFunction(list:List<UnitRecord>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.sdvUnitImage.setImageURI(mList[absoluteAdapterPosition].picURL)
            itemView.tvUnitName.text=mList[absoluteAdapterPosition].name
            itemView.tvUnitAddress.text=mList[absoluteAdapterPosition].primaryAddress.formatted

            if (mList[absoluteAdapterPosition].isDefault){
                itemView.ivDelete.visibility=View.GONE
            }else{
                itemView.ivDelete.visibility=View.VISIBLE
            }

            itemView.ivEdit.setOnClickListener {
                (mFragment as UnitListener).onEdit(mList[absoluteAdapterPosition],absoluteAdapterPosition)
            }

            itemView.ivDelete.setOnClickListener {
                (mFragment as UnitListener).onDelete(absoluteAdapterPosition,mList[absoluteAdapterPosition].id)
            }

        }
    }

    interface UnitListener{
        fun onEdit(unitRecord: UnitRecord, absoluteAdapterPosition: Int)
        fun onDelete(position: Int, id: String)
    }

}