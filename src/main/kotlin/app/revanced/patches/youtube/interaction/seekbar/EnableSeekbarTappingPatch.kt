package app.revanced.patches.youtube.interaction.seekbar

import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.all.misc.resources.addResources
import app.revanced.patches.all.misc.resources.addResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.interaction.seekbar.fingerprints.onTouchEventHandlerFingerprint
import app.revanced.patches.youtube.interaction.seekbar.fingerprints.seekbarTappingFingerprint
import app.revanced.patches.youtube.misc.integrations.integrationsPatch
import app.revanced.patches.youtube.misc.settings.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.settingsPatch
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Suppress("unused")
val enableSeekbarTappingPatch = bytecodePatch(
    name = "Seekbar tapping",
    description = "Adds an option to enable tap-to-seek on the seekbar of the video player.",
) {
    dependsOn(
        integrationsPatch,
        settingsPatch,
        addResourcesPatch
    )

    compatibleWith(
        "com.google.android.youtube"(
            "18.43.45",
            "18.44.41",
            "18.45.43",
            "18.48.39",
            "18.49.37",
            "19.01.34",
            "19.02.39",
            "19.03.36",
            "19.04.38",
            "19.05.36",
            "19.06.39",
            "19.07.40",
            "19.08.36",
            "19.09.38",
            "19.10.39",
            "19.11.43",
        ),
    )

    val onTouchEventHandlerResult by onTouchEventHandlerFingerprint
    val seekbarTappingResult by seekbarTappingFingerprint

    execute {
        addResources(this)

        PreferenceScreen.SEEKBAR.addPreferences(
            SwitchPreference("revanced_seekbar_tapping"),
        )

        val seekbarTappingMethods = onTouchEventHandlerResult.let {
            val patternScanResult = it.scanResult.patternScanResult!!

            fun getReference(index: Int) = it.mutableMethod.getInstruction<ReferenceInstruction>(index)
                .reference as MethodReference

            buildMap {
                put("N", getReference(patternScanResult.startIndex))
                put("O", getReference(patternScanResult.endIndex))
            }
        }

        seekbarTappingResult.let {
            val insertIndex = it.scanResult.patternScanResult!!.endIndex - 1

            it.mutableMethod.apply {
                val thisInstanceRegister = getInstruction<Instruction35c>(insertIndex - 1).registerC

                val freeRegister = 0
                val xAxisRegister = 2

                val oMethod = seekbarTappingMethods["O"]!!
                val nMethod = seekbarTappingMethods["N"]!!

                fun MethodReference.toInvokeInstructionString() =
                    "invoke-virtual { v$thisInstanceRegister, v$xAxisRegister }, $this"

                addInstructionsWithLabels(
                    insertIndex,
                    """
                        invoke-static { }, Lapp/revanced/integrations/youtube/patches/SeekbarTappingPatch;->seekbarTappingEnabled()Z
                        move-result v$freeRegister
                        if-eqz v$freeRegister, :disabled
                        ${oMethod.toInvokeInstructionString()}
                        ${nMethod.toInvokeInstructionString()}
                    """,
                    ExternalLabel("disabled", getInstruction(insertIndex)),
                )
            }
        }
    }
}
