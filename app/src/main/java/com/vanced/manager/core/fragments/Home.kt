package com.vanced.manager.core.fragments

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.vanced.manager.R
import com.vanced.manager.core.base.BaseFragment
import com.vanced.manager.ui.MainActivity
import com.vanced.manager.utils.MiuiHelper

open class Home : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pm = activity?.packageManager

        //Damn that's a lot of buttons
        val microginstallbtn = view.findViewById<MaterialButton>(R.id.microg_installbtn)
        val vancedinstallbtn = view.findViewById<MaterialButton>(R.id.vanced_installbtn)

        val bravebtn = view.findViewById<Button>(R.id.brave_button)
        val websitebtn = view.findViewById<Button>(R.id.website_button)
        val discordbtn = view.findViewById<Button>(R.id.discordbtn)
        val telegrambtn = view.findViewById<Button>(R.id.tgbtn)
        val twitterbtn = view.findViewById<Button>(R.id.twitterbtn)
        val redditbtn = view.findViewById<Button>(R.id.redditbtn)

        val microguninstallbtn = view.findViewById<ImageView>(R.id.microg_uninstallbtn)
        val microgsettingsbtn = view.findViewById<ImageView>(R.id.microg_settingsbtn)
        val vanceduninstallbtn = view.findViewById<ImageView>(R.id.vanced_uninstallbtn)

        val microgProgress = view.findViewById<ProgressBar>(R.id.microg_progress)
        val prefs = activity?.getSharedPreferences("installPrefs", Context.MODE_PRIVATE)
        val isVancedDownloading: Boolean? = prefs?.getBoolean("isVancedDownloading", false)
        val isMicrogDownloading: Boolean? = prefs?.getBoolean("isMicrogDownloading", false)

        //we need to check whether these apps are installed or not
        val microgStatus = pm?.let { isPackageInstalled("com.mgoogle.android.gms", it) }
        val vancedStatus =
            if (PreferenceManager.getDefaultSharedPreferences(activity).getString("vanced_variant", "Nonroot") == "Root") {
                pm?.let { isPackageInstalled("com.google.android.youtube", it) }
            }
            else {
                pm?.let { isPackageInstalled("com.vanced.android.youtube", it) }
            }

        vancedinstallbtn.setOnClickListener {
            if (!isVancedDownloading!!) {
                val mainActivity = (activity as MainActivity)
                if (PreferenceManager.getDefaultSharedPreferences(activity).getString("vanced_variant", "Nonroot") == "Root") {
                    if (MiuiHelper.isMiui()) {
                        mainActivity.secondMiuiDialog()
                    } else
                        mainActivity.rootModeDetected()
                } else {
                    if (MiuiHelper.isMiui()) {
                        mainActivity.secondMiuiDialog()
                    }
                }
                try {
                    activity?.cacheDir?.deleteRecursively()
                } catch (e: Exception) {
                    Log.d("VMCache", "Unable to delete cacheDir")
                }
                if (prefs.getBoolean("valuesModified", false)) {
                    val loadBar = view.findViewById<ProgressBar>(R.id.vanced_progress)
                    val dlText = view.findViewById<TextView>(R.id.vanced_downloading)
                    val loadCircle = view.findViewById<ProgressBar>(R.id.vanced_installing)
                    downloadArch(loadBar!!, dlText!!, loadCircle!!)
                    prefs.edit().putBoolean("isInstalling", false).apply()
                } else
                    view.findNavController().navigate(R.id.toInstallThemeFragment)
            } else {
                Toast.makeText(activity, "Please wait until installation finishes", Toast.LENGTH_SHORT).show()
            }

        }

        microginstallbtn.setOnClickListener {
            if (!isMicrogDownloading!!) {
                val dlText = view.findViewById<TextView>(R.id.microg_downloading)
                try {
                    installApk(
                        "https://x1nto.github.io/VancedFiles/microg.json",
                        microgProgress,
                        dlText
                    )
                } catch (e: Exception) {
                    Toast.makeText(activity, "Unable to start installation", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Please wait until installation finishes", Toast.LENGTH_SHORT).show()
            }
        }

        /*
        val microgVerText = view.findViewById<TextView>(R.id.microg_installed_version)
        if (microgStatus!!) {
            val microgVer = pm.getPackageInfo("com.mgoogle.android.gms", 0).versionName

            microguninstallbtn.setOnClickListener {
                uninstallApk("com.mgoogle.android.gms")
            }


            microgsettingsbtn.setOnClickListener {
                try {
                    val intent = Intent()
                    intent.component = ComponentName(
                        "com.mgoogle.android.gms",
                        "org.microg.gms.ui.SettingsActivity"
                    )
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(activity, "App not installed", Toast.LENGTH_SHORT).show()
                    activity?.recreate()
                }

            }


            //microgVerText.text = microgVer
        } else {
            //microgsettingsbtn.visibility = View.INVISIBLE
            //microguninstallbtn.visibility = View.INVISIBLE
            microgVerText.text = getString(R.string.unavailable)
            vancedinstallbtn.isEnabled =  PreferenceManager.getDefaultSharedPreferences(activity).getString("vanced_variant", "Nonroot") != "Nonroot"
            if (!vancedinstallbtn.isEnabled) {
                vancedinstallbtn.backgroundTintList = ColorStateList.valueOf(Color.DKGRAY)
                vancedinstallbtn.setTextColor(ColorStateList.valueOf(Color.GRAY))
                vancedinstallbtn.text = activity?.getString(R.string.no_microg)
                vancedinstallbtn.icon = null
            }
        }
         */

        val vancedVerText = view.findViewById<TextView>(R.id.vanced_installed_version)
        /*if (vancedStatus!!) {
            val vancedVer =
                if (PreferenceManager.getDefaultSharedPreferences(activity).getString("vanced_variant", "Nonroot") == "Root")
                    pm.getPackageInfo("com.google.android.youtube", 0).versionName
                else
                    pm.getPackageInfo("com.vanced.android.youtube", 0).versionName
            vanceduninstallbtn.setOnClickListener {
                if (PreferenceManager.getDefaultSharedPreferences(activity).getString("vanced_variant", "Nonroot") == "Root")
                    uninstallApk("com.vanced.android.youtube")
                else
                    uninstallApk("com.google.android.youtube")
            }
            vancedVerText.text = vancedVer
        } else {
            //vanceduninstallbtn.visibility = View.INVISIBLE
            vancedVerText.text = getString(R.string.unavailable)
        }
         */

        bravebtn.setOnClickListener {
            openUrl("https://brave.com/van874", R.color.Brave)

        }
        websitebtn.setOnClickListener {
            openUrl("https://vanced.app", R.color.Vanced)
        }
        discordbtn.setOnClickListener {
            openUrl("https://discord.gg/TUVd7rd", R.color.Discord)

        }
        telegrambtn.setOnClickListener {
            openUrl("https://t.me/joinchat/AAAAAEHf-pi4jH1SDlAL4w", R.color.Telegram)

        }
        twitterbtn.setOnClickListener {
            openUrl("https://twitter.com/YTVanced", R.color.Twitter)

        }
        redditbtn.setOnClickListener {
            openUrl("https://reddit.com/r/vanced", R.color.Reddit)
        }

    }

    override fun onResume() {
        super.onResume()
        val loadBar = view?.findViewById<ProgressBar>(R.id.vanced_progress)
        val dlText = view?.findViewById<TextView>(R.id.vanced_downloading)
        val loadCircle = view?.findViewById<ProgressBar>(R.id.vanced_installing)
        val prefs = activity?.getSharedPreferences("installPrefs", Context.MODE_PRIVATE)
        val isInstalling = prefs?.getBoolean("isInstalling", false)
        if (isInstalling!!) {
            downloadArch(loadBar!!, dlText!!, loadCircle!!)
            prefs.edit().putBoolean("isInstalling", false).apply()
        }
    }

}