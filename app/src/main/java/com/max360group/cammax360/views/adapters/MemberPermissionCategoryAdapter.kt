package com.max360group.cammax360.views.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PermissionsBitValues
import com.max360group.cammax360.repository.models.model.JobsCategory
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.EditMemberPermissionInterface
import kotlinx.android.synthetic.main.load_member_permission_category_layout.view.*

class MemberPermissionCategoryAdapter(
    var mFragment: Fragment,
    var mList: List<JobsCategory>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mPermissionsBitValues = PermissionsBitValues()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_member_permission_category_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ListViewHolder).bindListView(position)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.tvName.text = mList[absoluteAdapterPosition].name

            when (absoluteAdapterPosition) {
                0 -> {
                    if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.base != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true

                    } else {
                        if (mPermissionsBitValues.view and mList[absoluteAdapterPosition].mJobs.base != 0) {
                            itemView.cbView.isChecked = true
                        }


                        if (mPermissionsBitValues.add and mList[absoluteAdapterPosition].mJobs.base != 0) {
                            itemView.cbAdd.isChecked = true
                        }


                        if (mPermissionsBitValues.edit and mList[absoluteAdapterPosition].mJobs.base != 0) {
                            itemView.cbEdit.isChecked = true
                        }


                        if (mPermissionsBitValues.delete and mList[absoluteAdapterPosition].mJobs.base != 0) {
                            itemView.cbDelete.isChecked = true
                        }

                        if (mPermissionsBitValues.timeLine and mList[absoluteAdapterPosition].mJobs.base != 0) {
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    //Set click listener
                    itemView.cbView.setOnClickListener {
                        if (itemView.cbView.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.base != 0) {
                                mList[absoluteAdapterPosition].mJobs.base=125
                            }

                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base - mPermissionsBitValues.view
                            itemView.cbView.isChecked = false


                        } else {
                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base + mPermissionsBitValues.view
                            itemView.cbView.isChecked = true
                        }
                    }

                    itemView.cbAdd.setOnClickListener {
                        if (itemView.cbAdd.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.base != 0) {
                                mList[absoluteAdapterPosition].mJobs.base=125
                            }

                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base - mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base + mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = true
                        }
                    }

                    itemView.cbEdit.setOnClickListener {
                        if (itemView.cbEdit.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.base != 0) {
                                mList[absoluteAdapterPosition].mJobs.base=125
                            }

                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base - mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base + mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = true
                        }
                    }

                    itemView.cbDelete.setOnClickListener {
                        if (itemView.cbDelete.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.base != 0) {
                                mList[absoluteAdapterPosition].mJobs.base=125
                            }
                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base - mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base + mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = true
                        }
                    }

                    itemView.cbTimeLine.setOnClickListener {
                        if (itemView.cbTimeLine.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.base != 0) {
                                mList[absoluteAdapterPosition].mJobs.base=125
                            }
                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base - mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.base + mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mList[absoluteAdapterPosition].mJobs.base = 1
                            itemView.cbAll.isChecked = false

                        } else {
                            itemView.cbAll.isChecked = true
                            mList[absoluteAdapterPosition].mJobs.base = mPermissionsBitValues.all

                        }
                        (mFragment as EditMemberPermissionInterface).onUpdateData(
                            absoluteAdapterPosition
                        )
                    }
                }

                1 -> {
                    if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true

                    } else {
                        if (mPermissionsBitValues.view and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                            itemView.cbView.isChecked = true
                        }


                        if (mPermissionsBitValues.add and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                            itemView.cbAdd.isChecked = true
                        }


                        if (mPermissionsBitValues.edit and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                            itemView.cbEdit.isChecked = true
                        }


                        if (mPermissionsBitValues.delete and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                            itemView.cbDelete.isChecked = true
                        }

                        if (mPermissionsBitValues.timeLine and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    //Set click listener
                    itemView.cbView.setOnClickListener {
                        if (itemView.cbView.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos=125
                            }

                            mList[absoluteAdapterPosition].mJobs.mediaPhotos =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos - mPermissionsBitValues.view
                            itemView.cbView.isChecked = false


                        } else {
                            mList[absoluteAdapterPosition].mJobs.mediaPhotos =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos + mPermissionsBitValues.view
                            itemView.cbView.isChecked = true
                        }
                    }

                    itemView.cbAdd.setOnClickListener {
                        if (itemView.cbAdd.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos=125
                            }
                            mList[absoluteAdapterPosition].mJobs.mediaPhotos =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos - mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.mediaPhotos =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos + mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = true
                        }
                    }

                    itemView.cbEdit.setOnClickListener {
                        if (itemView.cbEdit.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos=125
                            }
                            mList[absoluteAdapterPosition].mJobs.mediaPhotos =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos - mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.mediaPhotos =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos + mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = true
                        }
                    }

                    itemView.cbDelete.setOnClickListener {
                        if (itemView.cbDelete.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos=125
                            }
                            mList[absoluteAdapterPosition].mJobs.mediaPhotos =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos - mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.mediaPhotos =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos + mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = true
                        }
                    }

                    itemView.cbTimeLine.setOnClickListener {
                        if (itemView.cbTimeLine.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.mediaPhotos != 0) {
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos=125
                            }
                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos - mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.base =
                                mList[absoluteAdapterPosition].mJobs.mediaPhotos + mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mList[absoluteAdapterPosition].mJobs.mediaPhotos = 1
                            itemView.cbAll.isChecked = false

                        } else {
                            itemView.cbAll.isChecked = true
                            mList[absoluteAdapterPosition].mJobs.mediaPhotos = mPermissionsBitValues.all

                        }
                        (mFragment as EditMemberPermissionInterface).onUpdateData(
                            absoluteAdapterPosition
                        )
                    }
                }

                2 -> {
                    if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true

                    } else {
                        if (mPermissionsBitValues.view and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                            itemView.cbView.isChecked = true
                        }


                        if (mPermissionsBitValues.add and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                            itemView.cbAdd.isChecked = true
                        }


                        if (mPermissionsBitValues.edit and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                            itemView.cbEdit.isChecked = true
                        }


                        if (mPermissionsBitValues.delete and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                            itemView.cbDelete.isChecked = true
                        }

                        if (mPermissionsBitValues.timeLine and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    //Set click listener
                    itemView.cbView.setOnClickListener {
                        if (itemView.cbView.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                                mList[absoluteAdapterPosition].mJobs.documents=125
                            }

                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents - mPermissionsBitValues.view
                            itemView.cbView.isChecked = false


                        } else {
                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents + mPermissionsBitValues.view
                            itemView.cbView.isChecked = true
                        }
                    }

                    itemView.cbAdd.setOnClickListener {
                        if (itemView.cbAdd.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                                mList[absoluteAdapterPosition].mJobs.documents=125
                            }
                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents - mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents + mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = true
                        }
                    }

                    itemView.cbEdit.setOnClickListener {
                        if (itemView.cbEdit.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                                mList[absoluteAdapterPosition].mJobs.documents=125
                            }
                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents - mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents + mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = true
                        }
                    }

                    itemView.cbDelete.setOnClickListener {
                        if (itemView.cbDelete.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                                mList[absoluteAdapterPosition].mJobs.documents=125
                            }

                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents - mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents + mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = true
                        }
                    }

                    itemView.cbTimeLine.setOnClickListener {
                        if (itemView.cbTimeLine.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.documents != 0) {
                                mList[absoluteAdapterPosition].mJobs.documents=125
                            }
                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents - mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.documents =
                                mList[absoluteAdapterPosition].mJobs.documents + mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mList[absoluteAdapterPosition].mJobs.documents = 1
                            itemView.cbAll.isChecked = false

                        } else {
                            itemView.cbAll.isChecked = true
                            mList[absoluteAdapterPosition].mJobs.documents = mPermissionsBitValues.all

                        }
                        (mFragment as EditMemberPermissionInterface).onUpdateData(
                            absoluteAdapterPosition
                        )
                    }
                }

                3 -> {
                    if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true

                    } else {
                        if (mPermissionsBitValues.view and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                            itemView.cbView.isChecked = true
                        }


                        if (mPermissionsBitValues.add and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                            itemView.cbAdd.isChecked = true
                        }


                        if (mPermissionsBitValues.edit and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                            itemView.cbEdit.isChecked = true
                        }


                        if (mPermissionsBitValues.delete and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                            itemView.cbDelete.isChecked = true
                        }

                        if (mPermissionsBitValues.timeLine and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    //Set click listener
                    itemView.cbView.setOnClickListener {
                        if (itemView.cbView.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                                mList[absoluteAdapterPosition].mJobs.conversations=125
                            }

                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations - mPermissionsBitValues.view
                            itemView.cbView.isChecked = false


                        } else {
                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations + mPermissionsBitValues.view
                            itemView.cbView.isChecked = true
                        }
                    }

                    itemView.cbAdd.setOnClickListener {
                        if (itemView.cbAdd.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                                mList[absoluteAdapterPosition].mJobs.conversations=125
                            }

                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations - mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations + mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = true
                        }
                    }

                    itemView.cbEdit.setOnClickListener {
                        if (itemView.cbEdit.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                                mList[absoluteAdapterPosition].mJobs.conversations=125
                            }

                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations - mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations + mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = true
                        }
                    }

                    itemView.cbDelete.setOnClickListener {
                        if (itemView.cbDelete.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                                mList[absoluteAdapterPosition].mJobs.conversations=125
                            }

                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations - mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations + mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = true
                        }
                    }

                    itemView.cbTimeLine.setOnClickListener {
                        if (itemView.cbTimeLine.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.conversations != 0) {
                                mList[absoluteAdapterPosition].mJobs.conversations=125
                            }

                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations - mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.conversations =
                                mList[absoluteAdapterPosition].mJobs.conversations + mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mList[absoluteAdapterPosition].mJobs.conversations = 1
                            itemView.cbAll.isChecked = false

                        } else {
                            itemView.cbAll.isChecked = true
                            mList[absoluteAdapterPosition].mJobs.conversations = mPermissionsBitValues.all

                        }
                        (mFragment as EditMemberPermissionInterface).onUpdateData(
                            absoluteAdapterPosition
                        )
                    }
                }

                4 -> {
                    if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true

                    } else {
                        if (mPermissionsBitValues.view and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                            itemView.cbView.isChecked = true
                        }


                        if (mPermissionsBitValues.add and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                            itemView.cbAdd.isChecked = true
                        }


                        if (mPermissionsBitValues.edit and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                            itemView.cbEdit.isChecked = true
                        }


                        if (mPermissionsBitValues.delete and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                            itemView.cbDelete.isChecked = true
                        }

                        if (mPermissionsBitValues.timeLine and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    //Set click listener
                    itemView.cbView.setOnClickListener {
                        if (itemView.cbView.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                                mList[absoluteAdapterPosition].mJobs.notes=125
                            }

                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes - mPermissionsBitValues.view
                            itemView.cbView.isChecked = false


                        } else {
                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes + mPermissionsBitValues.view
                            itemView.cbView.isChecked = true
                        }
                    }

                    itemView.cbAdd.setOnClickListener {
                        if (itemView.cbAdd.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                                mList[absoluteAdapterPosition].mJobs.notes=125
                            }
                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes - mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes + mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = true
                        }
                    }

                    itemView.cbEdit.setOnClickListener {
                        if (itemView.cbEdit.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                                mList[absoluteAdapterPosition].mJobs.notes=125
                            }
                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes - mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes + mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = true
                        }
                    }

                    itemView.cbDelete.setOnClickListener {
                        if (itemView.cbDelete.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                                mList[absoluteAdapterPosition].mJobs.notes=125
                            }
                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes - mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes + mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = true
                        }
                    }

                    itemView.cbTimeLine.setOnClickListener {
                        if (itemView.cbTimeLine.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.notes != 0) {
                                mList[absoluteAdapterPosition].mJobs.notes=125
                            }
                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes - mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.notes =
                                mList[absoluteAdapterPosition].mJobs.notes + mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mList[absoluteAdapterPosition].mJobs.notes = 1
                            itemView.cbAll.isChecked = false

                        } else {
                            itemView.cbAll.isChecked = true
                            mList[absoluteAdapterPosition].mJobs.notes = mPermissionsBitValues.all

                        }
                        (mFragment as EditMemberPermissionInterface).onUpdateData(
                            absoluteAdapterPosition
                        )
                    }
                }

                5 -> {
                    if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true

                    } else {
                        if (mPermissionsBitValues.view and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                            itemView.cbView.isChecked = true
                        }


                        if (mPermissionsBitValues.add and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                            itemView.cbAdd.isChecked = true
                        }


                        if (mPermissionsBitValues.edit and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                            itemView.cbEdit.isChecked = true
                        }


                        if (mPermissionsBitValues.delete and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                            itemView.cbDelete.isChecked = true
                        }

                        if (mPermissionsBitValues.timeLine and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    //Set click listener
                    itemView.cbView.setOnClickListener {
                        if (itemView.cbView.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                                mList[absoluteAdapterPosition].mJobs.comments=125
                            }

                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments - mPermissionsBitValues.view
                            itemView.cbView.isChecked = false


                        } else {
                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments + mPermissionsBitValues.view
                            itemView.cbView.isChecked = true
                        }
                    }

                    itemView.cbAdd.setOnClickListener {
                        if (itemView.cbAdd.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                                mList[absoluteAdapterPosition].mJobs.comments=125
                            }
                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments - mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments + mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = true
                        }
                    }

                    itemView.cbEdit.setOnClickListener {
                        if (itemView.cbEdit.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                                mList[absoluteAdapterPosition].mJobs.comments=125
                            }
                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments - mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments + mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = true
                        }
                    }

                    itemView.cbDelete.setOnClickListener {
                        if (itemView.cbDelete.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                                mList[absoluteAdapterPosition].mJobs.comments=125
                            }
                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments - mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments + mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = true
                        }
                    }

                    itemView.cbTimeLine.setOnClickListener {
                        if (itemView.cbTimeLine.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.comments != 0) {
                                mList[absoluteAdapterPosition].mJobs.comments=125
                            }
                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments - mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.comments =
                                mList[absoluteAdapterPosition].mJobs.comments + mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mList[absoluteAdapterPosition].mJobs.comments = 1
                            itemView.cbAll.isChecked = false

                        } else {
                            itemView.cbAll.isChecked = true
                            mList[absoluteAdapterPosition].mJobs.comments = mPermissionsBitValues.all

                        }
                        (mFragment as EditMemberPermissionInterface).onUpdateData(
                            absoluteAdapterPosition
                        )
                    }
                }

                6 -> {
                    if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.members != 0) {
                        itemView.cbAll.isChecked = true
                        itemView.cbView.isChecked = true
                        itemView.cbAdd.isChecked = true
                        itemView.cbEdit.isChecked = true
                        itemView.cbDelete.isChecked = true
                        itemView.cbTimeLine.isChecked = true

                    } else {
                        if (mPermissionsBitValues.view and mList[absoluteAdapterPosition].mJobs.members != 0) {
                            itemView.cbView.isChecked = true
                        }


                        if (mPermissionsBitValues.add and mList[absoluteAdapterPosition].mJobs.members != 0) {
                            itemView.cbAdd.isChecked = true
                        }


                        if (mPermissionsBitValues.edit and mList[absoluteAdapterPosition].mJobs.members != 0) {
                            itemView.cbEdit.isChecked = true
                        }


                        if (mPermissionsBitValues.delete and mList[absoluteAdapterPosition].mJobs.members != 0) {
                            itemView.cbDelete.isChecked = true
                        }

                        if (mPermissionsBitValues.timeLine and mList[absoluteAdapterPosition].mJobs.members != 0) {
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    //Set click listener
                    itemView.cbView.setOnClickListener {
                        if (itemView.cbView.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.members != 0) {
                                mList[absoluteAdapterPosition].mJobs.members=125
                            }

                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members - mPermissionsBitValues.view
                            itemView.cbView.isChecked = false


                        } else {
                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members + mPermissionsBitValues.view
                            itemView.cbView.isChecked = true
                        }
                    }

                    itemView.cbAdd.setOnClickListener {
                        if (itemView.cbAdd.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.members != 0) {
                                mList[absoluteAdapterPosition].mJobs.members=125
                            }

                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members - mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members + mPermissionsBitValues.add
                            itemView.cbAdd.isChecked = true
                        }
                    }

                    itemView.cbEdit.setOnClickListener {
                        if (itemView.cbEdit.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.members != 0) {
                                mList[absoluteAdapterPosition].mJobs.members=125
                            }

                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members - mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members + mPermissionsBitValues.edit
                            itemView.cbEdit.isChecked = true
                        }
                    }

                    itemView.cbDelete.setOnClickListener {
                        if (itemView.cbDelete.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.members != 0) {
                                mList[absoluteAdapterPosition].mJobs.members=125
                            }

                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members - mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members + mPermissionsBitValues.delete
                            itemView.cbDelete.isChecked = true
                        }
                    }

                    itemView.cbTimeLine.setOnClickListener {
                        if (itemView.cbTimeLine.isChecked) {
                            //If all is checked and Uncheck the specific value
                            if (mPermissionsBitValues.all and mList[absoluteAdapterPosition].mJobs.members != 0) {
                                mList[absoluteAdapterPosition].mJobs.members=125
                            }

                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members - mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = false
                        } else {
                            mList[absoluteAdapterPosition].mJobs.members =
                                mList[absoluteAdapterPosition].mJobs.members + mPermissionsBitValues.timeLine
                            itemView.cbTimeLine.isChecked = true
                        }
                    }

                    itemView.cbAll.setOnClickListener {
                        if (itemView.cbAll.isChecked) {
                            mList[absoluteAdapterPosition].mJobs.members = 1
                            itemView.cbAll.isChecked = false

                        } else {
                            itemView.cbAll.isChecked = true
                            mList[absoluteAdapterPosition].mJobs.members = mPermissionsBitValues.all

                        }
                        (mFragment as EditMemberPermissionInterface).onUpdateData(
                            absoluteAdapterPosition
                        )
                    }
                }

            }
        }
    }


}