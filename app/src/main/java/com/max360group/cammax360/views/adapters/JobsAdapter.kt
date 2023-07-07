package com.max360group.cammax360.views.adapters

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Job
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.JobsListener
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.load_jobs_image.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*
import java.lang.Exception

open class JobsAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mShowFullDetail = true
    private var mList = ArrayList<Job>()
    private var isLoadMore = false
    private var mUsersList = mutableListOf<UserX>()

    companion object {
        private const val VIEW_ITEM = 1
        private const val VIEW_MORE = 2
        const val LIMIT = 10
    }

    protected val mUserPrefsManager: UserPrefsManager by lazy { UserPrefsManager(mFragment.requireContext()) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            ListViewHolder(parent.inflate(R.layout.load_jobs_layout))
        } else {
            MoreViewHolder(parent.inflate(R.layout.row_load_more))
        }
    }

    override fun getItemCount(): Int {
        return mList.size + 1
    }

    fun updateData(list: List<Job>? = null, isShowFullDetail: Boolean, mSkip: Int) {
        mShowFullDetail = isShowFullDetail

        if (list != null) {
            if (mSkip == 0) {
                mList.clear()
                mUsersList.clear()
            }
            mList.addAll(list)
            isLoadMore = list.size >= LIMIT
        }

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
            (mFragment.requireContext().getString(R.string.st_photos) +
                    "(" + mList[absoluteAdapterPosition].medias!!.size.toString() + ")").also {
                itemView.tvPhotos.text = it
            }

            itemView.tvJobName.text = mList[absoluteAdapterPosition].title
            itemView.tvAddress.text = mList[absoluteAdapterPosition].address.formatted
            (mFragment.getString(R.string.st_due) + GeneralFunctions.changeDateFormat(
                mList[absoluteAdapterPosition].endDt,
                Constants.DATE_FORMAT_SERVER_ISO,
                Constants.DATE_FORMAT_DISPLAY
            )).also { itemView.tvDate.text = it }

            if (mList[absoluteAdapterPosition].details.contributionUpdatedAt != null) {
                (mFragment.getString(R.string.st_most_recent_contribution) +
                        GeneralFunctions.changeDateFormat(
                            mList[absoluteAdapterPosition].details.contributionUpdatedAt,
                            Constants.DATE_FORMAT_SERVER_ISO,
                            Constants.DATE_FORMAT_DISPLAY
                        )).also { itemView.tvMostRecent.text = it }
            }

            if (mShowFullDetail) {
                itemView.tvPhotos.visibility = View.VISIBLE
                itemView.rvImageList.visibility = View.VISIBLE
                itemView.tvContributors.visibility = View.VISIBLE
                itemView.rvContributionList.visibility = View.VISIBLE
                itemView.tvMostRecent.visibility = View.VISIBLE

                if (mList[absoluteAdapterPosition].medias!!.isEmpty()) {
                    itemView.rvImageList.visibility = View.GONE
                } else {
                    itemView.rvImageList.visibility = View.VISIBLE
                }

            } else {
                itemView.tvPhotos.visibility = View.GONE
                itemView.rvImageList.visibility = View.GONE
                itemView.tvContributors.visibility = View.GONE
                itemView.rvContributionList.visibility = View.GONE
                itemView.tvMostRecent.visibility = View.GONE
            }

            //update images adapter
            for (i in mList[absoluteAdapterPosition].medias!!.indices) {
                if (mList[absoluteAdapterPosition].medias!![i].subKind == "simple" || mList[absoluteAdapterPosition].medias!![i].subKind == "dual") {
                    if (mList[absoluteAdapterPosition].medias!![i].medias!!.isNotEmpty()) {

                        val imageName =
                            mList[absoluteAdapterPosition].medias!![0].medias!![0].media.split("${JobsConstants.JOB_KIND_PHOTO}/")
                        // Check if local file exists, if it exists set file from local else downloads from server
                        val file = GeneralFunctions
                            .getLocalMediaFile(mFragment.requireActivity(), imageName[1])
                        if (file.exists()) {
                            itemView.ivSdvImage.setImageURI(
                                GeneralFunctions.getLocalImageFile(file)
                            )
                        } else {
                            itemView.ivSdvImage.setImageURI(mList[absoluteAdapterPosition].medias!![0].medias!![0].mediaURL)
                        }


                        //Set image adapter
                        val mJobsImageAdapter =
                            JobsImageAdapter(mFragment, mList[absoluteAdapterPosition].medias!!)
                        itemView.rvImageList.adapter = mJobsImageAdapter
                    }
                }
            }

            //Add contribution users
            try {
                for (mData in mList[absoluteAdapterPosition].users!!) {
                    if (mData.details.contributionCount == 0) {
                        mList[absoluteAdapterPosition].users!!.remove(mData)
                    }
                }
            } catch (E: Exception) {

            }

            //Set contribution adapter
            val mJobsContributionsAdapter =
                JobsContributionsAdapter(mFragment, mList[absoluteAdapterPosition].users!!)
            itemView.rvContributionList.adapter = mJobsContributionsAdapter

            itemView.setOnClickListener {
                (mFragment as JobsListener).onItemClick(
                    mList[absoluteAdapterPosition].id,
                    mList[absoluteAdapterPosition].jobLocalId.toString()
                )
            }

            itemView.ivDirection.setOnClickListener {
                (mFragment as JobsListener).onMapCall(
                    mList[absoluteAdapterPosition].address.location.coordinates!![0],
                    mList[absoluteAdapterPosition].address.location.coordinates!![1]
                )
            }

            itemView.ivCamera.setOnClickListener {
                popUpMenu(
                    itemView.ivCamera,
                    absoluteAdapterPosition,
                    mList[absoluteAdapterPosition].id,
                    mList[absoluteAdapterPosition].jobLocalId
                )
            }

        }
    }

    private fun popUpMenu(ivCamera: ImageView, position: Int, id: String, mJobLocalId: Int) {
        val wrapper: Context =
            ContextThemeWrapper(mFragment.requireContext(), R.style.StylePopupMenu)
        val popUp = PopupMenu(wrapper, ivCamera, Gravity.END)
        popUp.menuInflater.inflate(R.menu.mediatype_pop_up_menu, popUp.menu)
        //Set Click Listener on Popup Menu Item
        popUp.setOnMenuItemClickListener { myItem ->
            val item = myItem!!.itemId

            when (item) {
                R.id.actionVideo -> {
                    (mFragment as JobsListener).onCameraClick(1, position, id, mJobLocalId)
                }

                R.id.actionImage -> {
                    (mFragment as JobsListener).onCameraClick(0, position, id, mJobLocalId)
                }


            }
            true
        }
        popUp.show()
    }

    private inner class MoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            if (GeneralFunctions.isInternetConnected(mFragment.requireContext())) {
                if (isLoadMore) {
                    itemView.llLoadMore.visibility = View.VISIBLE
                    (mFragment as JobsListener).onApiCall()
                } else {
                    itemView.llLoadMore.visibility = View.GONE
                }
            }
        }
    }
}