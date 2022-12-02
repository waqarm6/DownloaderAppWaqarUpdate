@file:Suppress("DEPRECATION")

package com.abtech.mp3.mp4.videodownloader.utils

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.abtech.mp3.mp4.videodownloader.R
import com.abtech.mp3.mp4.videodownloader.models.instawithlogin.CarouselMedia
import com.abtech.mp3.mp4.videodownloader.models.instawithlogin.ModelInstaWithLogin
import java.lang.reflect.Type
import java.util.*
import org.json.JSONObject


object Constants {

    //  const val TiktokApi: String = "https://api2-16-h2.musical.ly/aweme/v1/aweme/detail/"
    const val TiktokApi: String = "https://api2.musical.ly/aweme/v1/playwm/detail/"

    //  const val TiktokApiNowatermark: String = "https://nodejsapidownloader.herokuapp.com/api"
    const val TiktokApiNowatermark: String = "http://localhost/img/test.php?url="

    const val Facebook_watch_api: String =
        "https://allvideotest123.000webhostapp.com/insta/fbtest.php?url="

    //http://localhost/img/test.php?url=https://vm.tiktok.com/ZSQTnNWu/
    const val DlApisUrl3: String = "https://iphoting-yt-dl-api.herokuapp.com/api/info?url="
    const val DlApisUrl2: String = "https://dlphpapis.herokuapp.com/api/info?url="
    const val DlApisUrl: String = "https://dlphpapis21.herokuapp.com/api/info?url="
//    const val DlApisUrl: String = "https://shafatdlapisserver.herokuapp.com/api/info?url="
    //https://dlphpapis.herokuapp.com/api/info?url=
    const val STARTFOREGROUND_ACTION =
        "com.abtech.mp3.mp4.videodownloader.action.startforeground"
    const val STOPFOREGROUND_ACTION =
        "com.abtech.mp3.mp4.videodownloader.action.stopforeground"
    const val PREF_APPNAME: String = "aiovidedownloader"
    const val PREF_CLIP: String = "tikVideoDownloader"
    const val FOLDER_NAME = "/WhatsApp/"
    const val FOLDER_NAME_Whatsappbusiness = "/WhatsApp Business/"
    const val FOLDER_NAME_Whatsapp_and11 = "/Android/media/com.whatsapp/WhatsApp/"
    const val FOLDER_NAME_Whatsapp_and11_B = "/Android/media/com.whatsapp.w4b/WhatsApp Business/"
    const val SAVE_FOLDER_NAME = "/Download/ABtech_downloader/"
    const val MY_ANDROID_10_IDENTIFIER_OF_FILE = "ABtech_downloader_"
    const val videoDownloaderFolderName = "ABtech_downloader"

    const val tiktokWebviewUrl = "https://www.tiktok.com/?lang=en"

    const val directoryInstaShoryDirectorydownload_videos = "/InstaStory/videos/"
    const val directoryInstaShoryDirectorydownload_images = "/InstaStory/images/"
    const val directoryInstaShoryDirectorydownload_audio = "/InstaStory/audios/"


    //TODO NOTE: Should make both false if you wqant to upload to the playstore
    const val showyoutube = false
    const val showsoundcloud = false

    //TODO NOTE: if you disable ads you can make this false
    const val show_Ads = true
    const val show_startappads = false
    const val show_earning_card_in_extrafragment = false


    var counter = 1
    var show_fb: Boolean? = false

    var myVideoUrlIs: String? = ""
    var myPhotoUrlIs: String? = ""
    lateinit var myprogressDD: ProgressDialog


    fun startInstaDownload(context: Context, Url: String) {

        System.err.println("workkkkkkkkk 4" + Url)

        try {
            System.err.println("workkkkkkkkk 4")


            val sharedPrefsFor = SharedPrefsForInstagram(context)
            val map = sharedPrefsFor.preference
            if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != "") {

                downloadInstagramImageOrVideodata(
                    context,
                    Url,
                    "ds_user_id=" + map.preferencE_USERID + "; sessionid=" + map.preferencE_SESSIONID
                )
            } else {
                downloadInstagramImageOrVideodata(
                    context,
                    Url,
                    iUtils.myInstagramTempCookies
                )
            }


        } catch (e: java.lang.Exception) {
            System.err.println("workkkkkkkkk 5")
            e.printStackTrace()
        }
    }


    fun downloadInstagramImageOrVideodata(
        context: Context,
        URL: String?,
        Cookie: String?
    ) {

        myprogressDD = ProgressDialog(context)
        myprogressDD.setMessage("Loading....")
        myprogressDD.setCancelable(false)
        myprogressDD.show()

        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)


        AndroidNetworking.get(URL!!)
            .setPriority(Priority.MEDIUM)
            .addHeaders("Cookie", Cookie!!)
            .addHeaders(
                "User-Agent",
                iUtils.UserAgentsList[j]
            )
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    println("wojfdjhfdjhtik yyyy $response")

                    try {
                        val listType: Type =
                            object : TypeToken<ModelInstaWithLogin?>() {}.type
                        val modelInstagramResponse: ModelInstaWithLogin = Gson().fromJson(
                            response.toString(),
                            listType
                        )
                        println("workkkkk777 " + modelInstagramResponse.items[0].code)
                        var myInstaUsername: String? = ""

                        if (modelInstagramResponse.items[0].mediaType == 8) {
                            myInstaUsername =
                                modelInstagramResponse.items[0].user.username + "_"

                            val modelGetEdgetoNode = modelInstagramResponse.items[0]
                            val modelEdNodeArrayList: List<CarouselMedia> =
                                modelGetEdgetoNode.carouselMedia
                            for (i in modelEdNodeArrayList.indices) {
                                if (modelEdNodeArrayList[i].mediaType == 2) {
                                    myVideoUrlIs =
                                        modelEdNodeArrayList[i].videoVersions[0].geturl()
                                    DownloadFileMain.startDownloading(
                                        context,
                                        myVideoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myVideoUrlIs
                                        ),
                                        ".mp4"
                                    )
                                    // etText.setText("");
                                    try {
                                        myprogressDD.dismiss()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelEdNodeArrayList[i].imageVersions2.candidates[0]
                                            .geturl()
                                    DownloadFileMain.startDownloading(
                                        context,
                                        myPhotoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myPhotoUrlIs
                                        ),
                                        ".png"
                                    )
                                    myPhotoUrlIs = ""
                                    try {
                                        myprogressDD.dismiss()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    // etText.setText("");
                                }
                            }
                        } else {
                            val modelGetEdgetoNode = modelInstagramResponse.items[0]
                            myInstaUsername =
                                modelInstagramResponse.items[0].user.username + "_"

                            if (modelGetEdgetoNode.mediaType == 2) {
                                myVideoUrlIs =
                                    modelGetEdgetoNode.videoVersions[0].geturl()
                                DownloadFileMain.startDownloading(
                                    context,
                                    myVideoUrlIs,
                                    myInstaUsername + iUtils.getVideoFilenameFromURL(
                                        myVideoUrlIs
                                    ),
                                    ".mp4"
                                )
                                try {
                                    myprogressDD.dismiss()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                myVideoUrlIs = ""
                            } else {
                                myPhotoUrlIs =
                                    modelGetEdgetoNode.imageVersions2.candidates[0].geturl()
                                DownloadFileMain.startDownloading(
                                    context,
                                    myPhotoUrlIs,
                                    myInstaUsername + iUtils.getVideoFilenameFromURL(
                                        myPhotoUrlIs
                                    ),

                                    ".png"
                                )
                                try {
                                    myprogressDD.dismiss()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                myPhotoUrlIs = ""
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        myprogressDD.dismiss()
                        val alertDialog = AlertDialog.Builder(context).create()
                        alertDialog.setTitle(context.getString(R.string.logininsta))
                        alertDialog.setMessage(context.getString(R.string.urlisprivate))
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel)
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                        alertDialog.show()

                    }

                }

                override fun onError(error: ANError) {
                    println("myresponseis111 exp " + error.message)
                    try {
                        println("response1122334455:   " + "Failed1 " + error.message)
                        myprogressDD.dismiss()
                        val alertDialog = AlertDialog.Builder(context).create()
                        alertDialog.setTitle(context.getString(R.string.logininsta))
                        alertDialog.setMessage(context.getString(R.string.urlisprivate))
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel)
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                        alertDialog.show()
                    } catch (e: Exception) {

                    }
                }
            })


    }


}