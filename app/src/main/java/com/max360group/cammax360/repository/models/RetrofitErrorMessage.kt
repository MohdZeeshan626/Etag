package com.max360group.cammax360.repository.models

import androidx.annotation.StringRes

data class RetrofitErrorMessage(@StringRes val errorResId: Int? = null,
                                val errorMessage: String? = null)