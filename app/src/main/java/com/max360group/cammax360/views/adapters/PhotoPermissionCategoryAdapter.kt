package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.repository.models.PermissionsBitValues
import com.max360group.cammax360.repository.models.model.Permissions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.EditMemberPermissionInterface
import com.max360group.cammax360.views.interfaces.PhotoDetailPermissionsListener
import kotlinx.android.synthetic.main.load_member_permission_category_layout.view.*


class PhotoPermissionCategoryAdapter(
    var mFragment: Fragment, var mJobs: Permissions
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mShowFullDetail = true
    private var mList = mutableListOf<String>("Photo", "Comments", "Members")
    private var mPermissionsBitValues = PermissionsBitValues()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_photo_permission_category_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(isShowFullDetail: Boolean) {
        mShowFullDetail = isShowFullDetail
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvName.text = mList[adapterPosition]

            when (adapterPosition) {
                0 -> {
                    if (mPermissionsBitValues.all and mJobs.base != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true
                    } else {
                        //Set click listener
                        itemView.cbView.setOnClickListener {
                            if (itemView.cbView.isChecked) {
                                mJobs.base =
                                    mJobs.base - mPermissionsBitValues.view
                                itemView.cbView.isChecked = false
                            } else {
                                mJobs.base =
                                    mJobs.base + mPermissionsBitValues.view
                                itemView.cbView.isChecked = true
                            }
                        }

                        itemView.cbAdd.setOnClickListener {
                            if (itemView.cbAdd.isChecked) {
                                mJobs.base =
                                    mJobs.base - mPermissionsBitValues.add
                                itemView.cbAdd.isChecked = false
                            } else {
                                mJobs.base =
                                    mJobs.base + mPermissionsBitValues.add
                                itemView.cbAdd.isChecked = true
                            }
                        }

                        itemView.cbEdit.setOnClickListener {
                            if (itemView.cbEdit.isChecked) {
                                mJobs.base =
                                    mJobs.base - mPermissionsBitValues.edit
                                itemView.cbEdit.isChecked = false
                            } else {
                                mJobs.base =
                                    mJobs.base + mPermissionsBitValues.edit
                                itemView.cbEdit.isChecked = true
                            }
                        }

                        itemView.cbDelete.setOnClickListener {
                            if (itemView.cbDelete.isChecked) {
                                mJobs.base =
                                    mJobs.base - mPermissionsBitValues.delete
                                itemView.cbDelete.isChecked = false
                            } else {
                                mJobs.base =
                                    mJobs.base + mPermissionsBitValues.delete
                                itemView.cbDelete.isChecked = true
                            }
                        }

                        itemView.cbTimeLine.setOnClickListener {
                            if (itemView.cbTimeLine.isChecked) {
                                mJobs.base =
                                    mJobs.base - mPermissionsBitValues.timeLine
                                itemView.cbTimeLine.isChecked = false
                            } else {
                                mJobs.base =
                                    mJobs.base + mPermissionsBitValues.timeLine
                                itemView.cbTimeLine.isChecked = true
                            }
                        }
                    }

                    //To select all permission type
                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mJobs.base =
                                mJobs.base - mPermissionsBitValues.all
                            itemView.cbAll.isChecked = false
                        } else {
                            itemView.cbAll.isChecked = true
                            mJobs.base = mPermissionsBitValues.all
                        }
                        (mFragment as PhotoDetailPermissionsListener).updateData(adapterPosition)
                    }

                    //To select all permission type
                    if (mPermissionsBitValues.view and mJobs.base != 0) {
                        itemView.cbView.isChecked = true
                    }

                    if (mPermissionsBitValues.add and mJobs.base != 0) {
                        itemView.cbAdd.isChecked = true
                    }

                    if (mPermissionsBitValues.edit and mJobs.base != 0) {
                        itemView.cbEdit.isChecked = true
                    }

                    if (mPermissionsBitValues.delete and mJobs.base != 0) {
                        itemView.cbDelete.isChecked = true
                    }

                    if (mPermissionsBitValues.timeLine and mJobs.base != 0) {
                        itemView.cbTimeLine.isChecked = true
                    }


                }

                1 -> {
                    if (mPermissionsBitValues.all and mJobs.comments != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true
                    } else {
                        //Set click listener
                        itemView.cbView.setOnClickListener {
                            if (itemView.cbView.isChecked) {
                                mJobs.comments =
                                    mJobs.comments - mPermissionsBitValues.view
                                itemView.cbView.isChecked = false
                            } else {
                                mJobs.comments =
                                    mJobs.comments + mPermissionsBitValues.view
                                itemView.cbView.isChecked = true
                            }
                        }

                        itemView.cbAdd.setOnClickListener {
                            if (itemView.cbAdd.isChecked) {
                                mJobs.comments =
                                    mJobs.comments - mPermissionsBitValues.add
                                itemView.cbAdd.isChecked = false
                            } else {
                                mJobs.comments =
                                    mJobs.comments + mPermissionsBitValues.add
                                itemView.cbAdd.isChecked = true
                            }
                        }

                        itemView.cbEdit.setOnClickListener {
                            if (itemView.cbEdit.isChecked) {
                                mJobs.comments =
                                    mJobs.comments - mPermissionsBitValues.edit
                                itemView.cbEdit.isChecked = false
                            } else {
                                mJobs.comments =
                                    mJobs.comments + mPermissionsBitValues.edit
                                itemView.cbEdit.isChecked = true
                            }
                        }
                        itemView.cbDelete.setOnClickListener {
                            if (itemView.cbDelete.isChecked) {
                                mJobs.comments =
                                    mJobs.comments - mPermissionsBitValues.delete
                                itemView.cbDelete.isChecked = false
                            } else {
                                mJobs.comments =
                                    mJobs.comments + mPermissionsBitValues.delete
                                itemView.cbDelete.isChecked = true
                            }
                        }

                        itemView.cbTimeLine.setOnClickListener {
                            if (itemView.cbTimeLine.isChecked) {
                                mJobs.comments =
                                    mJobs.comments - mPermissionsBitValues.timeLine
                                itemView.cbTimeLine.isChecked = false
                            } else {
                                mJobs.comments =
                                    mJobs.comments + mPermissionsBitValues.timeLine
                                itemView.cbTimeLine.isChecked = true
                            }
                        }
                    }

                    //To select all permission type
                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mJobs.comments =
                                mJobs.comments - mPermissionsBitValues.all
                            itemView.cbAll.isChecked = false
                        } else {
                            itemView.cbAll.isChecked = true
                            mJobs.comments = mPermissionsBitValues.all
                        }
                        (mFragment as PhotoDetailPermissionsListener).updateData(adapterPosition)
                    }

                    //To select all permission type
                    if (mPermissionsBitValues.view and mJobs.comments != 0) {
                        itemView.cbView.isChecked = true
                    }
                    if (mPermissionsBitValues.add and mJobs.comments != 0) {
                        itemView.cbAdd.isChecked = true
                    }
                    if (mPermissionsBitValues.edit and mJobs.comments != 0) {
                        itemView.cbEdit.isChecked = true
                    }

                    if (mPermissionsBitValues.delete and mJobs.comments != 0) {
                        itemView.cbDelete.isChecked = true
                    }

                    if (mPermissionsBitValues.timeLine and mJobs.comments != 0) {
                        itemView.cbTimeLine.isChecked = true
                    }
                }

                2 -> {
                    if (mPermissionsBitValues.all and mJobs.members != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true
                    } else {
                        //Set click listener
                        itemView.cbView.setOnClickListener {
                            if (itemView.cbView.isChecked) {
                                mJobs.members =
                                    mJobs.members - mPermissionsBitValues.view
                                itemView.cbView.isChecked = false
                            } else {
                                mJobs.members =
                                    mJobs.members + mPermissionsBitValues.view
                                itemView.cbView.isChecked = true
                            }
                        }

                        itemView.cbAdd.setOnClickListener {
                            if (itemView.cbAdd.isChecked) {
                                mJobs.members =
                                    mJobs.members - mPermissionsBitValues.add
                                itemView.cbAdd.isChecked = false
                            } else {
                                mJobs.members =
                                    mJobs.members + mPermissionsBitValues.add
                                itemView.cbAdd.isChecked = true
                            }
                        }

                        itemView.cbEdit.setOnClickListener {
                            if (itemView.cbEdit.isChecked) {
                                mJobs.members =
                                    mJobs.members - mPermissionsBitValues.edit
                                itemView.cbEdit.isChecked = false
                            } else {
                                mJobs.members =
                                    mJobs.members + mPermissionsBitValues.edit
                                itemView.cbEdit.isChecked = true
                            }
                        }

                        itemView.cbDelete.setOnClickListener {
                            if (itemView.cbDelete.isChecked) {
                                mJobs.members =
                                    mJobs.members - mPermissionsBitValues.delete
                                itemView.cbDelete.isChecked = false
                            } else {
                                mJobs.members =
                                    mJobs.members + mPermissionsBitValues.delete
                                itemView.cbDelete.isChecked = true
                            }
                        }

                        itemView.cbTimeLine.setOnClickListener {
                            if (itemView.cbTimeLine.isChecked) {
                                mJobs.members =
                                    mJobs.members - mPermissionsBitValues.timeLine
                                itemView.cbTimeLine.isChecked = false
                            } else {
                                mJobs.members =
                                    mJobs.members + mPermissionsBitValues.timeLine
                                itemView.cbTimeLine.isChecked = true

                            }
                        }
                    }

                    //To select all permission type
                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mJobs.members =
                                mJobs.members - mPermissionsBitValues.all
                            itemView.cbAll.isChecked = false
                        } else {
                            itemView.cbAll.isChecked = true
                            mJobs.members = mPermissionsBitValues.all
                        }
                        (mFragment as PhotoDetailPermissionsListener).updateData(adapterPosition)
                    }

                    //Set permissions
                    if (mPermissionsBitValues.view and mJobs.members != 0) {
                        itemView.cbView.isChecked = true
                    }

                    if (mPermissionsBitValues.add and mJobs.members != 0) {
                        itemView.cbAdd.isChecked = true
                    }

                    if (mPermissionsBitValues.edit and mJobs.members != 0) {
                        itemView.cbEdit.isChecked = true
                    }

                    if (mPermissionsBitValues.delete and mJobs.members != 0) {
                        itemView.cbDelete.isChecked = true
                    }

                    if (mPermissionsBitValues.timeLine and mJobs.members != 0) {
                        itemView.cbTimeLine.isChecked = true
                    }

                }

            }
        }
    }


}