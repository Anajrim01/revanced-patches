package app.revanced.patches.piccomafr.misc

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.piccomafr.misc.fingerprints.getAndroidIdFingerprint

@Suppress("unused")
val spoofAndroidDeviceIdPatch = bytecodePatch(
    name = "Spoof Android device ID",
    description = "Spoofs the Android device ID used by the app for account authentication." +
            "This can be used to copy the account to another device.",
    use = false,
) {
    compatibleWith(
        "com.piccomaeurope.fr"(
            "6.4.0",
            "6.4.1",
            "6.4.2",
            "6.4.3",
            "6.4.4",
            "6.4.5",
            "6.5.0",
            "6.5.1",
            "6.5.2",
            "6.5.3",
            "6.5.4",
            "6.6.0",
            "6.6.1",
            "6.6.2"
        )
    )

    val getAndroidIDResult by getAndroidIdFingerprint

    execute {
        getAndroidIDResult.mutableMethod.addInstructions(
            0,
            """
            const-string v0, "0011223344556677"
            return-object v0
            """
        )
    }
}