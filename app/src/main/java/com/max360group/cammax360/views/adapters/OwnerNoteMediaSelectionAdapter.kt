package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.NoteMedia
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.NotesHistoryInterface
import kotlinx.android.synthetic.main.load_images_layout.view.ivCancelImage
import kotlinx.android.synthetic.main.load_note_media_selection.view.*
import java.io.File

class OwnerNoteMediaSelectionAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<NoteMedia>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_note_media_selection))
    }

    override fun getItemCount(): Int {
       return mList.size
    }

    fun updateData(list:List<NoteMedia>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            when (mList[absoluteAdapterPosition].kind) {
                "photo" -> {
                    itemView.sdvMediaImage.setImageURI(GeneralFunctions.getLocalImageFile(File(mList[absoluteAdapterPosition].media)))
                }
                "video" -> {
                    itemView.sdvMediaImage.setImageURI(GeneralFunctions.getLocalImageFile(File(mList[absoluteAdapterPosition].thumbnail)))

                }
                else -> {
                    itemView.sdvMediaImage.setImageResource(R.drawable.ic_document)
                }
            }

            itemView.ivCancelImage.setOnClickListener {
                (mFragment as NotesHistoryInterface).onDeleteMedia(absoluteAdapterPosition)
            }

        }
    }
}