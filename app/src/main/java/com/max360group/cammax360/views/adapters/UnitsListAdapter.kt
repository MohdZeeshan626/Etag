package com.max360group.cammax360.views.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.EmailsModel
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.GeneralInfoInterface
import kotlinx.android.synthetic.main.layout_email.view.*


class UnitsListAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mEmailList = mutableListOf<EmailsModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.list_property_unit))
    }

    override fun getItemCount(): Int {
        return mEmailList.size
    }

    fun updateData(list: List<EmailsModel>) {
        mEmailList.clear()
        mEmailList.addAll(list)
        notifyDataSetChanged()
    }

    fun getEmails(): List<EmailsModel> {
        return mEmailList
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            if (absoluteAdapterPosition == 0) {
                itemView.ivDelete.visibility = View.GONE
            } else {
                itemView.ivDelete.visibility = View.VISIBLE
                itemView.ivDelete.setOnClickListener {
                    (mFragment as GeneralInfoInterface).onDeleteEmail(
                        absoluteAdapterPosition
                    )
                    notifyDataSetChanged()
                }
            }

            itemView.etType.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    mEmailList[absoluteAdapterPosition].type=s.toString()
                }
            })

            itemView.etEmail.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    mEmailList[absoluteAdapterPosition].email=s.toString()
                }
            })

        }
    }
}