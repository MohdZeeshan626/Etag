package com.max360group.cammax360.views.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Comments
import com.max360group.cammax360.repository.models.ConversationList
import com.max360group.cammax360.repository.models.DashBoardMenuModel
import com.max360group.cammax360.repository.models.Rm
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_DISPLAY1
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.dialog_fragment_camera.view.*
import kotlinx.android.synthetic.main.layout_email.view.*
import kotlinx.android.synthetic.main.layout_integration_auth.view.*
import kotlinx.android.synthetic.main.load_conversation_layout.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_organisation_layout.view.*


class AuthIntegrationAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        var RM_DISPLAY = ""
        var IS_RM_SYNC = false
    }

    private var mRm = Rm()
    private var isSyncRm=true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_integration_auth))
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    fun updateData(rm: Rm,syncRm:Boolean=true) {
        mRm = rm
        isSyncRm=syncRm
        notifyDataSetChanged()
    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            if (mRm.displayName.isNotBlank()) {
                itemView.cbSync.isChecked = true
                itemView.tvDisplayName.visibility = View.VISIBLE
                itemView.etMessage.visibility = View.VISIBLE
                itemView.etMessage.setText(mRm.displayName)
                RM_DISPLAY=mRm.displayName
            }

            itemView.etMessage.addTextChangedListener(object : TextWatcher {
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
                    RM_DISPLAY = s.toString()
                }
            })

            itemView.cbSync.setOnClickListener {
                if (isSyncRm) {
                    if (itemView.cbSync.isChecked) {
                        itemView.cbSync.isChecked = false
                        itemView.tvDisplayName.visibility = View.GONE
                        itemView.etMessage.visibility = View.GONE
                    } else {
                        itemView.cbSync.isChecked = true
                        itemView.tvDisplayName.visibility = View.VISIBLE
                        itemView.etMessage.visibility = View.VISIBLE
                    }
                }else{
                    if (itemView.cbSync.isChecked) {
                        itemView.cbSync.isChecked = false
                        IS_RM_SYNC=false
                    } else {
                        itemView.cbSync.isChecked = true
                        IS_RM_SYNC=true
                    }
                }
            }
        }
    }
}