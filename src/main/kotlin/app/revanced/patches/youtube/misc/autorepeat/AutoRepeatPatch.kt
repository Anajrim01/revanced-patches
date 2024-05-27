package app.revanced.patches.youtube.misc.autorepeat

import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstructions
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.all.misc.resources.addResources
import app.revanced.patches.all.misc.resources.addResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.misc.integrations.integrationsPatch
import app.revanced.patches.youtube.misc.settings.PreferenceScreen
import app.revanced.patches.youtube.shared.fingerprints.autoRepeatFingerprint
import app.revanced.patches.youtube.shared.fingerprints.autoRepeatParentFingerprint
import app.revanced.util.resultOrThrow

@Suppress("unused")
val autoRepeatPatch = bytecodePatch(
    name = "Always repeat",
    description = "Adds an option to always repeat videos when they end.",
) {
    dependsOn(
        integrationsPatch,
        addResourcesPatch,
    )

    compatibleWith(
        "com.google.android.youtube"(
            "18.32.39",
            "18.37.36",
            "18.38.44",
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

    val autoRepeatParentResult by autoRepeatParentFingerprint

    execute { context ->
        addResources("youtube", "misc.autorepeat.AutoRepeatPatch")

        PreferenceScreen.MISC.addPreferences(
            SwitchPreference("revanced_auto_repeat"),
        )

        autoRepeatFingerprint.apply {
            resolve(context, autoRepeatParentResult.classDef)
        }.resultOrThrow().mutableMethod.apply {
            val playMethod = autoRepeatParentResult.mutableMethod
            val index = getInstructions().lastIndex

            // Remove return-void.
            removeInstruction(index)
            // Add own instructions there.
            addInstructionsWithLabels(
                index,
                """
                    invoke-static {}, Lapp/revanced/integrations/youtube/patches/AutoRepeatPatch;->shouldAutoRepeat()Z
                    move-result v0
                    if-eqz v0, :noautorepeat
                    invoke-virtual { p0 }, $playMethod
                    :noautorepeat
                    return-void
                """,
            )
        }
    }
}
