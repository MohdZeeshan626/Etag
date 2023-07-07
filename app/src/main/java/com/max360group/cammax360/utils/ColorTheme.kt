package com.max360group.cammax360.utils

import com.max360group.cammax360.repository.models.Theme

object ColorTheme {
    fun getThemeColor(): List<Theme> {
        var theme = ArrayList<Theme>()
        theme.add(Theme(primary = "#4FD44C", primaryLight = "#5f4cd44d"))
        theme.add(Theme(primary = "#5F4CD4", primaryLight = "#5f4cd4"))
        theme.add(Theme(primary = "#C295CC", primaryLight = "#C295CC4d"))
        theme.add(Theme(primary = "#FF5959", primaryLight = "#FF59594d"))
        theme.add(Theme(primary = "#FFD5A7", primaryLight = "#FFD5A74d"))
        theme.add(Theme(primary = "#4D3670", primaryLight = "#4D36704d"))
        return theme
    }
}