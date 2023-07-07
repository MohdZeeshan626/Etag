package com.max360group.cammax360.views.adapters

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.widget.PopupWindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.layout_owner_poppup_menu.view.*
import kotlinx.android.synthetic.main.layout_owner_poppup_menu.view.tvStatus
import kotlinx.android.synthetic.main.load_owners_layout.view.*
import kotlinx.android.synthetic.main.load_owners_layout.view.ivMenu
import kotlinx.android.synthetic.main.load_owners_layout.view.sdvPropertyImage
import kotlinx.android.synthetic.main.load_owners_layout.view.tvPropertyName
import kotlinx.android.synthetic.main.load_properties_layout.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*
import java.io.File


open class OwnersAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = ArrayList<UserOwner>()
    private var isLoadMore = false

    companion object {
        private const val VIEW_ITEM = 1
        private const val VIEW_MORE = 2
        const val LIMIT = 10
    }

    protected val mUserPrefsManager: UserPrefsManager by lazy {
        UserPrefsManager(mFragment.requireContext())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            ListViewHolder(parent.inflate(R.layout.load_owners_layout))
        } else {
            MoreViewHolder(parent.inflate(R.layout.row_load_more))
        }
    }

    override fun getItemCount(): Int {
        return mList.size + 1
    }

    fun updateData(list: List<UserOwner>, mSkip: Int) {
        if (mSkip == 0) {
            mList.clear()
        }
        mList.addAll(list)
        isLoadMore = list.size >= LIMIT
        notifyDataSetChanged()

    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            mList.size -> VIEW_MORE
            else -> VIEW_ITEM
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        if (VIEW_MORE == getItemViewType(i)) {
            (holder as MoreViewHolder).bindListView(i)
        } else {
            (holder as ListViewHolder).bindListView(i)
        }
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            if (mList[absoluteAdapterPosition].picURL.startsWith("https://")) {
                itemView.sdvPropertyImage.setImageURI(mList[absoluteAdapterPosition].picURL)
            } else {
                itemView.sdvPropertyImage.setImageURI(GeneralFunctions.getLocalImageFile(File(mList[absoluteAdapterPosition].picURL)))
            }

            (mList[absoluteAdapterPosition].firstName + " " + mList[absoluteAdapterPosition].lastName).also {
                itemView.tvPropertyName.text = it
            }

            itemView.tvEmail.text = mList[absoluteAdapterPosition].email
            itemView.tvEmail.setOnClickListener {
                GeneralFunctions.email(mList[absoluteAdapterPosition].email!!,mFragment.requireContext())
            }

            if (mList[absoluteAdapterPosition].isActive) {
                itemView.tvStatus.text = mFragment.getString(R.string.st_active)
            } else {
                itemView.tvStatus.text = mFragment.getString(R.string._st_inactive)
            }

            itemView.setOnClickListener {
                (mFragment as OwnerListener).onItemClick(
                    mList[absoluteAdapterPosition].id,
                    mList[absoluteAdapterPosition].ownerLocalId
                )
            }

            itemView.ivMenu.setOnClickListener {
                popUpMenu(itemView.ivMenu, absoluteAdapterPosition)
            }
        }
    }

    private fun popUpMenu(ivCamera: ImageView, position: Int) {
        val inflater =
            mFragment.requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.layout_owner_poppup_menu, null)
        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.elevation = 10f // give it shadow
        PopupWindowCompat.showAsDropDown(popupWindow, ivCamera, 100, 10, Gravity.LEFT)
        PopupWindowCompat.setWindowLayoutType(
            popupWindow,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //Set view
        popupView.swStatus.isChecked = mList[position].isActive

        popupView.tvDelete.setOnClickListener {
            (mFragment as OwnerListener).onDeleteOwner(
                mList[position].id,
                mList[position].ownerLocalId
            )
            popupWindow.dismiss()
        }
        popupView.tvInvite.setOnClickListener {
            (mFragment as OwnerListener).onSendInvite(
                mList[position].id,
                mList[position].ownerLocalId,
                mList[position].isSendInvite
            )
            popupWindow.dismiss()
        }

        popupView.tvEditDetail.setOnClickListener {
            (mFragment as OwnerListener).onEditDetail(
                mList[position].id,
                mList[position].ownerLocalId
            )
            popupWindow.dismiss()
        }

        popupView.swStatus.setOnClickListener {
            if (mList[position].isActive) {
                mList[position].isActive = false
                (mFragment as OwnerListener).onStatus(
                    mList[position].id,
                    mList[position].ownerLocalId,
                    false
                )
            } else {
                mList[position].isActive = true
                (mFragment as OwnerListener).onStatus(
                    mList[position].id,
                    mList[position].ownerLocalId,
                    true
                )
            }
            popupWindow.dismiss()
            notifyDataSetChanged()
        }

    }

    private inner class MoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            if (GeneralFunctions.isInternetConnected(mFragment.requireContext())) {
                if (isLoadMore) {
                    itemView.llLoadMore.visibility = View.VISIBLE
                    (mFragment as OwnerListener).onLoadMore()
                } else {
                    itemView.llLoadMore.visibility = View.GONE
                }
            }
        }
    }

    interface OwnerListener {
        fun onItemClick(id: String, ownerLocalId: Int)
        fun onLoadMore()
        fun onDeleteOwner(id: String, ownerLocalId: Int)
        fun onStatus(id: String, status1: Int, status: Boolean)
        fun onSendInvite(id: String, localId: Int, sendInvite: Boolean)
        fun onEditDetail(id: String, ownerLocalId: Int)
    }
}