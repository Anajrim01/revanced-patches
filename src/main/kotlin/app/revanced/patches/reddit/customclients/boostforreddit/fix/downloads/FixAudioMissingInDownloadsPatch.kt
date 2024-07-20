package app.revanced.patches.reddit.customclients.boostforreddit.fix.downloads

import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.reddit.customclients.boostforreddit.fix.downloads.fingerprints.downloadAudioFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Suppress("unused")
val fixAudioMissingInDownloadsPatch = bytecodePatch(
    name = "Fix missing audio in video downloads",
    description = "Fixes audio missing in videos downloaded from v.redd.it.",
) {
    compatibleWith("com.rubenmayayo.reddit")

    val downloadAudioResult by downloadAudioFingerprint

    execute {
        val endpointReplacements = mapOf(
            "/DASH_audio.mp4" to "/DASH_AUDIO_128.mp4",
            "/audio" to "/DASH_AUDIO_64.mp4",
        )

        downloadAudioResult.scanResult.stringsScanResult!!.matches.forEach { match ->
            downloadAudioResult.mutableMethod.apply {
                val replacement = endpointReplacements[match.string]
                val register = getInstruction<OneRegisterInstruction>(match.index).registerA

                replaceInstruction(match.index, "const-string v$register, \"$replacement\"")
            }
        }
    }
}
