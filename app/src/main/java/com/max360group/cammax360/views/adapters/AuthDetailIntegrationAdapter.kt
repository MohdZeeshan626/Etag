package com.max360group.cammax360.views.adapters

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
import kotlinx.android.synthetic.main.layout_detail_auth_integration.view.*
import kotlinx.android.synthetic.main.layout_integration_auth.view.*
import kotlinx.android.synthetic.main.load_conversation_layout.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_organisation_layout.view.*

class AuthDetailIntegrationAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var rmFields=Rm()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_detail_auth_integration))
    }

    override fun getItemCount(): Int {
        return 1
    }

    fun updateFunction(rm: Rm) {
        rmFields=rm
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.rmDisplayName.text=rmFields.displayName
        }
    }
}