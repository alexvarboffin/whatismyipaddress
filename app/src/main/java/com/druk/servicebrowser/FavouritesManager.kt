package com.druk.servicebrowser

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import com.druk.servicebrowser.ui.RegTypeActivity
import com.walhalla.whatismyipaddress.R
import com.walhalla.whatismyipaddress.TApp.Companion.getRegTypeManager
import java.util.LinkedList

class FavouritesManager internal constructor(private val context: Context) {
    private val sharedPreferences: SharedPreferences
    private val regTypeManager: RegTypeManager?
    private val favouriteRegTypes: MutableSet<String>

    init {
        sharedPreferences = context.getSharedPreferences("DEFAULT", Context.MODE_PRIVATE)
        regTypeManager = getRegTypeManager(context)
        favouriteRegTypes = HashSet<String>(sharedPreferences.getAll().keys)
    }

    fun isFavourite(regType: String?): Boolean {
        return favouriteRegTypes.contains(regType)
    }

    fun addToFavourites(regType: String?) {
        val success = favouriteRegTypes.add(regType!!)
        if (success) {
            sharedPreferences.edit().putBoolean(regType, true).apply()
            updateDynamicShortcuts()
        }
    }

    fun removeFromFavourites(regType: String?) {
        val success = favouriteRegTypes.remove(regType)
        if (success) {
            sharedPreferences.edit().remove(regType).apply()
            updateDynamicShortcuts()
        }
    }

    fun updateDynamicShortcuts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            return
        }

        val shortcutManager =
            context.getSystemService<ShortcutManager?>(ShortcutManager::class.java)
        if (shortcutManager == null) {
            return
        }

        val shortcuts: MutableList<ShortcutInfo?> = LinkedList<ShortcutInfo?>()

        for (regType in favouriteRegTypes) {
            var fullNameRegType = regTypeManager!!.getRegTypeDescription(regType)
            if (fullNameRegType == null) {
                fullNameRegType = regType
            }
            val newShortcut = ShortcutInfoCompat.Builder(context, regType)
                .setShortLabel(fullNameRegType)
                .setLongLabel(fullNameRegType)
                .setIcon(
                    IconCompat.createWithResource(
                        context,
                        R.drawable.ic_star_accent
                    )
                )
                .setIntent(
                    RegTypeActivity.createIntent(context, regType, Config.LOCAL_DOMAIN).setAction(
                        Intent.ACTION_VIEW
                    )
                )
                .build()

            shortcuts.add(newShortcut.toShortcutInfo())
        }

        shortcutManager.dynamicShortcuts = shortcuts
    }
}
