package com.techo.chattv.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.techo.chattv.R
import com.techo.chattv.adapter.CountryListAdapter
import com.techo.chattv.databinding.ActivityFileLoadBinding
import com.techo.chattv.model.Country
import com.techo.chattv.utils.*
import com.techo.chattv.utils.FileReader
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


class CountrySelectActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private lateinit var binding: ActivityFileLoadBinding
    private var countryArrayList: ArrayList<Country> = ArrayList()
    private lateinit var adapter : CountryListAdapter
    var url = ""
    var fileDownloadStarted = false
    lateinit var interstitialAds: InterstitialAds
    lateinit var ipTvRealm : IPTvRealm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileLoadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        ipTvRealm = IPTvRealm()
        interstitialAds = InterstitialAds(this)
        interstitialAds.loadInterstitialAds()
        interstitialAds.loadAdmobAds()

        NativeAds().loadNativeBannerFBAd(this,binding.bannerFrameLay)

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.BLACK
        if (ipTvRealm.allChannelCount() > 0) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            countryArrayList =
                Gson().fromJson(
                    readJsonFromAsset(this),
                    object : TypeToken<List<Country?>?>() {}.type
                )
            adapter = CountryListAdapter(this, countryArrayList)
            binding.countryListRv.adapter = adapter
            adapter.onCountryClick = {
                FileReaderFromAssets(this,it,interstitialAds).readFile()
//                if (it.isNotEmpty() && it != ""){
//                    url = "https://iptv-org.github.io/iptv/countries/$it.m3u"
//                    try {
//                        if (hasReadFilePermission()) {
//                            //File read permission success
//                            if (!fileDownloadStarted && url != "" && url.contains(".m3u")) {
//                                downloadFiles(url)
//                            } else {
//                                toastMessage(getString(R.string.select_m3u_file_message))
//                            }
//                        } else {
//                            EasyPermissions.requestPermissions(
//                                this,
//                                getString(R.string.rationale_read_file),
//                                IPTV_READ_DOC_PERM,
//                                Manifest.permission.READ_EXTERNAL_STORAGE
//                            )
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
            }
            Log.e("countryArrayList","Size = ${countryArrayList.size}")
            binding.searchET.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                    // TODO Auto-generated method stub
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                    // TODO Auto-generated method stub
                }

                override fun afterTextChanged(s: Editable) {
                    filter(s.toString())
                }
            })
            Log.e("arrayList", countryArrayList.toString())
        }
    }

    private fun hasReadFilePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    fun filter(text: String?) {
        val search: MutableList<Country> = ArrayList()
        for (d in countryArrayList) {
            if (d.name.contains(text.toString())) {
                search.add(d)
            }
        }
        if(search.isNotEmpty()){
            binding.listMsg.visibility = View.GONE
        } else {
            binding.listMsg.visibility = View.VISIBLE
        }
        adapter.updateList(search)
    }

    private fun readJsonFromAsset(context: Context): String? {
        return try {
            val open = context.assets.open("customCountryList.json")
            val bArr = ByteArray(open.available())
            open.read(bArr)
            open.close()
            String(bArr, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.e("onPermissionsGranted", "Granted")
        if (!fileDownloadStarted && url != "" && url.contains(".m3u")) {
            downloadFiles(url)
        } else {
            toastMessage(getString(R.string.select_m3u_file_message))
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.e("onPermissionsDenied",  requestCode.toString() + ":" + perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Timber.d("onRationaleAccepted:%s", requestCode)
    }

    override fun onRationaleDenied(requestCode: Int) {
        Timber.d("onRationaleDenied:%s", requestCode)
    }

    private fun downloadFiles(fileUrl: String) {
        val thread = Thread {
            if (CommonFunction().isOnline(this)) {
                try {
                    progressVisibility(visible = true, fileDownload = true)
                    Log.e("fileUrl", fileUrl)
                    val fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1, fileUrl.length)
                    Log.e("fileName", fileName)
                    val filePath =
                        Environment.getExternalStorageDirectory()
                            .toString() + "/Download/" + fileName
                    val file = File(filePath)
                    val u = URL(fileUrl)
                    val `is` = u.openStream()
                    writeStreamToFile(`is`, file, fileName)
                } catch (mue: MalformedURLException) {
                    progressVisibility(visible = false, fileDownload = false)
                    Log.e("SYNC getUpdate", "malformed url error", mue)
                } catch (ioe: IOException) {
                    progressVisibility(visible = false, fileDownload = false)
                    Log.e("SYNC getUpdate", "io error", ioe)
                } catch (se: SecurityException) {
                    progressVisibility(visible = false, fileDownload = false)
                    Log.e("SYNC getUpdate", "security error", se)
                }
            } else {
                CommonFunction().showToast(this,getString(com.techo.chattv.R.string.network_issue_message))
            }
        }
        thread.start()
    }

    private fun writeStreamToFile(input: InputStream, file: File, filename: String) {
        try {
            if (!file.exists()) {
                FileOutputStream(file).use { output ->
                    val buffer =
                        ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            progressVisibility(visible = false, fileDownload = false)
        } catch (e: IOException) {
            e.printStackTrace()
            progressVisibility(visible = false, fileDownload = false)
        } finally {
            try {
                Log.e("Download", "Complete")
                if (file.exists()) {
                    val countryCode = filename.substring(0, filename.lastIndexOf("."))
                    val loc = Locale("", countryCode.toUpperCase(Locale.ROOT))
                    SaveSharedPreference(this).setCountry(this, loc.displayCountry)
                    FileReader(this, Uri.fromFile(file),interstitialAds).readFile()
                    progressVisibility(visible = false, fileDownload = false)
                } else {
                    toastMessage(getString(R.string.file_not_download))
                    progressVisibility(visible = false, fileDownload = false)
                }
                input.close()
            } catch (e: IOException) {
                progressVisibility(visible = false, fileDownload = false)
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val IPTV_READ_DOC_PERM = 123
    }

    private fun progressVisibility(visible: Boolean, fileDownload: Boolean) {
        runOnUiThread {
            fileDownloadStarted = fileDownload
            if (visible) {
                binding.progressbar.visibility = View.VISIBLE
            } else {
                binding.progressbar.visibility = View.GONE
            }
        }
    }

    private fun toastMessage(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}