package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Note
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_conversation_layout.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_owner_notes_layout.view.*
import kotlinx.android.synthetic.main.load_owner_notes_layout.view.sdvUserImage
import kotlinx.android.synthetic.main.load_owner_notes_layout.view.tvDate


class OwnerNotesAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<Note>()
     val mUserPrefsManager: UserPrefsManager by lazy {
        UserPrefsManager(mFragment.requireContext()) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_owner_notes_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateFunction(list: List<Note>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.sdvUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)
            itemView.tvName.text=mUserPrefsManager.loginedUser!!.firstName
            itemView.tvNote.text=mList[absoluteAdapterPosition].note
            itemView.tvDate.text= GeneralFunctions.changeUtcToLocal(mList[absoluteAdapterPosition].createdAt)

            val mOwnerNotesImageAdapter=OwnerNotesImageAdapter(mFragment,mList[absoluteAdapterPosition].medias!!)
            itemView.rvImages.adapter=mOwnerNotesImageAdapter

        }
    }
}