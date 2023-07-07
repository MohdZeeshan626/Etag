package com.max360group.cammax360.views.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PhoneNumberModel
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.GeneralInfoInterface
import kotlinx.android.synthetic.main.layout_email.view.ivDelete
import kotlinx.android.synthetic.main.layout_phone_list.view.*


class PhoneListAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mPhoneList= mutableListOf<PhoneNumberModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_phone_list))
    }

    override fun getItemCount(): Int {
        return mPhoneList.size
    }

    fun updateData(list:List<PhoneNumberModel>){
        mPhoneList.clear()
        mPhoneList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            if (absoluteAdapterPosition==0){
                itemView.ivDelete.visibility=View.GONE
            }else{
                itemView.ivDelete.visibility=View.VISIBLE
                itemView.ivDelete.setOnClickListener {
                    (mFragment as GeneralInfoInterface).onDeletePhone(absoluteAdapterPosition)
                }
            }

            itemView.etName.setText(mPhoneList[absoluteAdapterPosition].name)
            itemView.etPhone.setText(mPhoneList[absoluteAdapterPosition].phoneNumber)
            itemView.etEx.setText(mPhoneList[absoluteAdapterPosition].extension)
            itemView.cbDefault.isChecked=mPhoneList[absoluteAdapterPosition].default
            itemView.cbTextMessage.isChecked=mPhoneList[absoluteAdapterPosition].textMessage

            itemView.cbDefault.setOnClickListener {
                if (itemView.cbDefault.isChecked){
                    itemView.cbDefault.isChecked=false
                    mPhoneList[absoluteAdapterPosition].default=false
                }else{
                    itemView.cbDefault.isChecked=true
                    mPhoneList[absoluteAdapterPosition].default=true
                }
            }
            itemView.cbTextMessage.setOnClickListener {
                if (itemView.cbTextMessage.isChecked){
                    itemView.cbTextMessage.isChecked=false
                    mPhoneList[absoluteAdapterPosition].textMessage=false
                }else{
                    itemView.cbTextMessage.isChecked=true
                    mPhoneList[absoluteAdapterPosition].textMessage=true
                }
            }

            itemView.etName.addTextChangedListener(object : TextWatcher {
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
                    mPhoneList[absoluteAdapterPosition].name=s.toString()
                }
            })

            itemView.etPhone.addTextChangedListener(object : TextWatcher {
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
                    mPhoneList[absoluteAdapterPosition].phoneNumber=s.toString()
                }
            })

            itemView.etEx.addTextChangedListener(object : TextWatcher {
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
                    mPhoneList[absoluteAdapterPosition].extension=s.toString()
                }
            })

        }
    }
}