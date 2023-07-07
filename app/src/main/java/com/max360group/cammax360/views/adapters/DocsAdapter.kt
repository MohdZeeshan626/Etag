package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.DocsModel
import com.max360group.cammax360.repository.models.model.VideosModel
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.PictureListener
import com.max360group.cammax360.views.interfaces.VideoListener
import kotlinx.android.synthetic.main.load_images_layout.view.*


class DocsAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<DocsModel>()

    private var mPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_docs_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(docsList: ArrayList<DocsModel>, position: Int) {
        mList.clear()
        mList.addAll(docsList)
        mPosition = position
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.sdvSimpleImage.setImageResource(R.drawable.ic_document)

            if (mPosition==adapterPosition){
                itemView.ivSelection.visibility=View.VISIBLE
            }else{
                itemView.ivSelection.visibility=View.GONE
            }

            itemView.ivCancelImage.setOnClickListener {
                (mFragment as DocsListener).onRemove(adapterPosition)
            }

            itemView.setOnClickListener {
                mPosition = adapterPosition
                (mFragment as DocsListener).onDocsClick(adapterPosition)
                notifyDataSetChanged()
            }

        }
    }

    interface DocsListener{
        fun onDocsClick(position:Int)
        fun onRemove(position:Int)
    }
}