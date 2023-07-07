package com.max360group.cammax360.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.ColorTheme
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.utils.CustomTypefaceSpan

import com.max360group.cammax360.views.fragments.BaseFragment


abstract class BaseAppCompactActivity : AppCompatActivity() {

    companion object {
         const val INTENT_EXTRAS_IS_FROM_NOTIFICATION = "isFromNotification"
    }

    protected val mUserPrefsManager: UserPrefsManager by lazy { UserPrefsManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set theme
        initTheme()
        setContentView(layoutId)
        if (GeneralFunctions.isAboveLollipopDevice) {
            val window = window
            if (isMakeStatusBarTransparent) {
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorTransparent)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }else{
                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
                getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

            }
        }
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        // Allows NavigationUI to support proper up navigation or the drawer layout
        // drawer menu, depending on the situation
        return if (null != navHostFragment) findNavController(navHostFragment!!).navigateUp() else false
    }

    fun closeActivity() {
        /**
         * Check if the activity was opened by notification then move to HomeActivity class else
         * normally move to the last fragment or activity in the stack
         */
        if (intent.getBooleanExtra(INTENT_EXTRAS_IS_FROM_NOTIFICATION, false)) {
            super.finish()
            startActivity(
                Intent(this, HomeActivity::class.java)
                    .putExtra(INTENT_EXTRAS_IS_FROM_NOTIFICATION, true)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        } else {
            super.finish()
        }

    }

    private fun initTheme() {
        if (mUserPrefsManager.loginedUser!=null) {
            when (mUserPrefsManager.loginedUser!!.theme!!.primary) {
                ColorTheme.getThemeColor()[0].primary -> {
                    theme.applyStyle(R.style.BaseAppTheme, true)
                }

                ColorTheme.getThemeColor()[1].primary -> {
                    theme.applyStyle(R.style.ThemeBlue, true)
                }

                ColorTheme.getThemeColor()[3].primary -> {
                    theme.applyStyle(R.style.ThemeRed, true)
                }

                ColorTheme.getThemeColor()[2].primary -> {
                    theme.applyStyle(R.style.ThemeMagenta, true)
                }

                ColorTheme.getThemeColor()[4].primary -> {
                    theme.applyStyle(R.style.ThemeOrange, true)
                }

                ColorTheme.getThemeColor()[5].primary -> {
                    theme.applyStyle(R.style.ThemePurple, true)
                }
                else -> {
                    theme.applyStyle(R.style.BaseAppTheme, true)
                }
            }
        }
    }


    abstract val layoutId: Int

    abstract val isMakeStatusBarTransparent: Boolean

    abstract fun init()

    abstract val navHostFragment: NavHostFragment?

    override fun onBackPressed() {
        super.onBackPressed()

    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun AppCompatActivity.doFragmentTransaction(
    fragManager: FragmentManager = supportFragmentManager,
    @IdRes containerViewId: Int,
    fragment: Fragment,
    tag: String = "",
    @AnimatorRes enterAnimation: Int = 0,
    @AnimatorRes exitAnimation: Int = 0,
    @AnimatorRes popEnterAnimation: Int = 0,
    @AnimatorRes popExitAnimation: Int = 0,
    isAddFragment: Boolean = true,
    isAddToBackStack: Boolean = true,
    allowStateLoss: Boolean = false
) {

    val fragmentTransaction = fragManager.beginTransaction()
        .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)

    if (isAddFragment) {
        fragmentTransaction.add(containerViewId, fragment, tag)
    } else {
        fragmentTransaction.replace(containerViewId, fragment, tag)
    }

    if (isAddToBackStack) {
        fragmentTransaction.addToBackStack(null)
    }

    if (allowStateLoss) {
        fragmentTransaction.commitAllowingStateLoss()
    } else {
        fragmentTransaction.commit()
    }


}

fun AppCompatActivity.openShareDialog(
    shareHeading: String = getString(R.string.share_via),
    shareSubject: String = getString(R.string.app_name),
    messageToShare: String
) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "text/plain"
    share.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
    share.putExtra(Intent.EXTRA_TEXT, messageToShare)
    startActivity(Intent.createChooser(share, shareHeading))
}

fun Menu.changeItemsFont(context: Context) {
    for (i in 0 until this.size()) {
        val menuItem = this.getItem(i)

        val spannableString = SpannableString(menuItem.title)
        val endSpan = spannableString.length

        // Set Typeface span
        spannableString.setSpan(
            CustomTypefaceSpan(
                ResourcesCompat.getFont(
                    context,
                    R.font.font_santral_reguler
                )
            ), 0, endSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set color span
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context,
                    R.color.colorWhite
                )
            ), 0, endSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        menuItem.title = spannableString
    }


}
