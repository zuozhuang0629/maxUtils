package com.applovin.sdk

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class MaxUtils {
    companion object {
        private var instance: MaxUtils? = null
            get() {
                if (field == null) {
                    field = MaxUtils()
                }
                return field
            }

        @Synchronized
        fun get(): MaxUtils {
            return instance!!
        }
    }

    lateinit var maxSdk: AppLovinSdk

    fun initSdk(context: Context, key: () -> String) {
        val set = AppLovinSdkSettings(context)
        maxSdk = AppLovinSdk.getInstance(
            key.invoke(),
            set,
            context
        )

        maxSdk.mediationProvider = "max"
        maxSdk.initializeSdk {
        }
    }

    var maxInter: MaxInterstitialAd? = null
    private var retryAttempt = 0.0
    private var isShowing = false
    private var lastShowTime = 0L


    fun getIsShowIng() = isShowing
    fun getLastShowTime() = lastShowTime

    /**
     * 插屏广告的初始化
     *
     * @param context
     * @param keyStr 插屏广告的key  id
     * @param aty 需要传一个activity进行初始化 ，不会内存泄漏
     * @return
     */
    fun initMaxInter(keyStr: String, aty: Activity): MaxInterstitialAd? {
        maxInter = MaxInterstitialAd(keyStr, maxSdk, aty)
        maxInter?.setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd?) {
                retryAttempt = 0.0
            }

            override fun onAdDisplayed(ad: MaxAd?) {
                isShowing = true
            }

            override fun onAdHidden(ad: MaxAd?) {
                isShowing = false
                maxInter?.loadAd()
                lastShowTime = System.currentTimeMillis()
                _callBack.invoke(true)
            }

            override fun onAdClicked(ad: MaxAd?) {
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {

                retryAttempt++
                val delayMillis = TimeUnit.SECONDS.toMillis(
                    2.0.pow(6.0.coerceAtMost(retryAttempt))
                        .toLong()
                )

                Looper.myLooper()?.let {
                    Handler(it).postDelayed({ maxInter?.loadAd() }, delayMillis)
                }
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                lastShowTime = System.currentTimeMillis()
                isShowing = false
                maxInter?.loadAd()
                _callBack.invoke(false)

            }

        })


        maxInter?.loadAd()
        return maxInter
    }

    private var _callBack: (Boolean) -> Unit = {}

    /**
     * 显示插屏广告
     *
     * @param isCanShow 是够需要显示广告
     * @param tag 显示广告的tag
     * @param closeCall 广告回调 false：显示失败 true ：显示成功
     * @receiver
     */
    fun showInter(isCanShow: Boolean = true, tag: String, closeCall: (Boolean) -> Unit) {
        if (maxInter == null) {
            closeCall.invoke(false)
            return
        }

        if (!maxInter!!.isReady) {
            closeCall.invoke(false)
            return
        }

        if (!isCanShow) {
            closeCall.invoke(false)
            return
        }

        _callBack = closeCall
        maxInter!!.showAd(tag)

    }

    private var bannerAdView: MaxAdView? = null

    /**
     * 显示banner
     *
     * @param context
     * @param keyStr banner key
     * @return
     */
    fun showMaxBanner(context: Context, keyStr: String, viewGroup: ViewGroup): MaxAdView? {
        bannerAdView = MaxAdView(keyStr, maxSdk, context)

        bannerAdView?.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        viewGroup.addView(bannerAdView)
        bannerAdView?.loadAd()
        return bannerAdView
    }

    private var nativeAdLoader: MaxNativeAdLoader? = null
    private var nativeAd: MaxAd? = null

    /**
     * 显示原生广告
     *
     * @param context
     * @param keyStr 原生广告的key id
     * @param viewGroup
     */
    fun showMaxNative(context: Context, keyStr: String, viewGroup: ViewGroup): MaxNativeAdLoader? {

        nativeAdLoader = MaxNativeAdLoader(keyStr, maxSdk, context)
        nativeAdLoader?.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
                if (nativeAd != null) {
                    nativeAdLoader?.destroy(nativeAd)
                }

                nativeAd = ad

                viewGroup.removeAllViews()
                viewGroup.addView(nativeAdView)
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                // We recommend retrying with exponentially higher delays up to a maximum delay
            }

            override fun onNativeAdClicked(ad: MaxAd) {
                // Optional click callback
            }

        })
        nativeAdLoader?.loadAd()
        return nativeAdLoader
    }
}