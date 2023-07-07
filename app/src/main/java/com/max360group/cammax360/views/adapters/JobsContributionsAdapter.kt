package com.max360group.cammax360.views.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.load_jobs_contribution.view.*
import java.lang.Exception


class JobsContributionsAdapter(
    var mFragment: Fragment,
    var mUsersList: List<UserX>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<Int>(
        R.color.colorPrimary,
        R.color.colorSkyBlue,
        R.color.colorPurple,
        R.color.colorRed
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_jobs_contribution))
    }

    override fun getItemCount(): Int {
        return if (mUsersList.size < 4) {
            mUsersList.size
        } else {
            4
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            var firstName = ""
            var lastName = ""

            //Get first and last char of name
            if (mUsersList[absoluteAdapterPosition].userId.firstName.isNotBlank()) {
                firstName = mUsersList[absoluteAdapterPosition].userId.firstName[0].toString()
            }
            if (mUsersList[absoluteAdapterPosition].userId.lastName.isNotBlank()) {
                lastName = mUsersList[absoluteAdapterPosition].userId.lastName[0].toString()
            }

            (firstName + lastName + "(" +
                    mUsersList[absoluteAdapterPosition].details.contributionCount + ")").also {
                itemView.tvItem.text = it
            }


            if (absoluteAdapterPosition == 0) {
                val img: Drawable = mFragment.resources.getDrawable(R.drawable.ic_star_batch)
                img.setBounds(0, 0, 35, 35)
                itemView.tvItem.setCompoundDrawables(null, null, img, null)
            }

            itemView.tvItem.backgroundTintList =
                ContextCompat.getColorStateList(mFragment.requireContext(), mList[absoluteAdapterPosition])
        }
    }
}