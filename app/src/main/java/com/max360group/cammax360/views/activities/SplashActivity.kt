package com.max360group.cammax360.views.activities

import android.content.Intent
import androidx.navigation.fragment.NavHostFragment
import com.max360group.cammax360.R
import java.util.*

class SplashActivity : BaseAppCompactActivity() {
    override val layoutId: Int
        get() = R.layout.fragment_splash


    override val isMakeStatusBarTransparent: Boolean
        get() = true

    override fun init() {
        android.os.Handler().postDelayed({
            if (mUserPrefsManager.isLogined) {
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1000)
    }

    override val navHostFragment: NavHostFragment?
        get() = null


}
