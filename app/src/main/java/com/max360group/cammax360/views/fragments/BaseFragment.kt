package com.max360group.cammax360.views.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.MarshMallowPermissions
import com.max360group.cammax360.utils.MyCustomLoader
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.MainActivity
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by Mukesh on 19/3/18.
 */
abstract class BaseFragment : Fragment() {

    protected val mMyCustomLoader: MyCustomLoader by lazy { MyCustomLoader(context) }
    protected val mUserPrefsManager: UserPrefsManager by lazy { UserPrefsManager(requireContext()) }
    protected val mMarshMallowPermissions by lazy {
        MarshMallowPermissions(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        container?.inflate(layoutRes = layoutId)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set Toolbar
        if (null != toolbar) {
            toolbar.setNavigationIcon(R.drawable.ic_back_white)
            toolbar.setNavigationOnClickListener {
                hideSoftKeyboard()
                (activityContext as BaseAppCompactActivity).onBackPressed()
            }
        }

        init(savedInstanceState)
        observeBaseProperties()
    }

    private fun observeBaseProperties() {
        // Observe message
        viewModel?.getSuccessMessage()?.observe(viewLifecycleOwner, Observer {
            showMessage(null, it)
        })

        // Observe any general exception
        viewModel?.getErrorHandler()?.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                showMessage(resId = it.getErrorResource(), isShowSnackbarMessage = false)
            }
        })

        // Observe user session expiration
        viewModel?.isSessionExpired()?.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                expireUserSession()
            }
        })

        // Observe visibility of loader
        viewModel?.isShowLoader()?.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                showProgressLoader()
            } else {
                hideProgressLoader()
            }
        })

        // Observe retrofit error messages
        viewModel?.getRetrofitErrorMessage()?.observe(viewLifecycleOwner, Observer {
            showMessage(
                resId = it?.errorResId,
                message = it?.errorMessage,
                isShowSnackbarMessage = false
            )
        })

        // Observe screen specific data
        observeProperties()
    }

    val activityContext: Context
        get() = requireActivity()

    fun showMessage(
        resId: Int? = null, message: String? = null,
        isShowSnackbarMessage: Boolean = false
    ) {
        if (isShowSnackbarMessage) {
            mMyCustomLoader.showSnackBar(view, message ?: getString(resId!!))
        } else {
            mMyCustomLoader.showToast(message ?: getString(resId!!))
        }
    }

    fun dismissDialogFragment() {
        (childFragmentManager.findFragmentByTag(getString(R.string.dialog)) as DialogFragment).dismiss()
    }

    protected fun navigateToMainActivity(activity:Activity=MainActivity()) {
        startActivity(
            Intent(activityContext, activity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        (requireContext() as BaseAppCompactActivity).finish()
    }

    protected fun showProgressLoader() {
        mMyCustomLoader.showProgressDialog()
    }

    protected fun hideProgressLoader() {
        mMyCustomLoader.dismissProgressDialog()
    }

    private fun expireUserSession() {
        showMessage(
            R.string.session_expired, null,
            false
        )
        startActivity(
            Intent(activity, MainActivity::class.java)
                .addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                )
        )
    }


    fun hideSoftKeyboard() {
        val inputMethodManager = activityContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    fun showSoftKeyboard() {
        (activityContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun openDialPad(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    open fun isConnected(context: Context): Boolean {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (null != cm) {
            val info = cm.activeNetworkInfo
            return info != null && info.isConnected
        }
        return false
    }


    abstract val layoutId: Int

    abstract fun init(savedInstanceState: Bundle?)

    abstract val viewModel: BaseViewModel?

    abstract fun observeProperties()

}