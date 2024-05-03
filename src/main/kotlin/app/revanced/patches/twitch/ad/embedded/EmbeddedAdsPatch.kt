package app.revanced.patches.twitch.ad.embedded

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.all.misc.resources.addResources
import app.revanced.patches.all.misc.resources.addResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.ListPreference
import app.revanced.patches.twitch.ad.embedded.fingerprints.createsUsherClientFingerprint
import app.revanced.patches.twitch.ad.video.VideoAdsPatch
import app.revanced.patches.twitch.misc.settings.SettingsPatch
import app.revanced.patches.twitch.misc.settings.SettingsPatch.PreferenceScreen
import app.revanced.patches.youtube.misc.integrations.integrationsPatch

@Suppress("unused")
val embeddedAdsPatch = bytecodePatch(
    name = "Block embedded ads",
    description = "Blocks embedded stream ads using services like Luminous or PurpleAdBlocker.",
) {
    dependsOn(
        VideoAdsPatch,
        integrationsPatch,
        SettingsPatch, 
        
    )

    compatibleWith("tv.twitch.android.app"("15.4.1", "16.1.0", "16.9.1"))

    val createUsherClientResult by createsUsherClientFingerprint

    execute {
        addResources(this)

        PreferenceScreen.ADS.SURESTREAM.addPreferences(
            ListPreference("revanced_block_embedded_ads", summaryKey = null),
        )

        // Inject OkHttp3 application interceptor
        createUsherClientResult.mutableMethod.addInstructions(
            3,
            """
                invoke-static  {}, Lapp/revanced/integrations/twitch/patches/EmbeddedAdsPatch;->createRequestInterceptor()Lapp/revanced/integrations/twitch/api/RequestInterceptor;
                move-result-object v2
                invoke-virtual {v0, v2}, Lokhttp3/OkHttpClient${"$"}Builder;->addInterceptor(Lokhttp3/Interceptor;)Lokhttp3/OkHttpClient${"$"}Builder;
            """,
        )
    }
}
