package app.revanced.patches.youtube.interaction.copyvideourl

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.youtube.misc.playercontrols.PlayerControlsBytecodePatch
import app.revanced.patches.youtube.video.information.VideoInformationPatch

@Suppress("unused")
val copyVideoUrlBytecodePatch = bytecodePatch(
    name = "Copy video URL",
    description = "Adds options to display buttons in the video player to copy video URLs.",
) {
    dependsOn(
        copyVideoUrlResourcePatch,
        PlayerControlsBytecodePatch,
        VideoInformationPatch,
    )

    compatibleWith(
        "com.google.android.youtube"(
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

    val integrationsPlayerPackage = "Lapp/revanced/integrations/youtube/videoplayer"
    val buttonsDescriptors = listOf(
        "$integrationsPlayerPackage/CopyVideoUrlButton;",
        "$integrationsPlayerPackage/CopyVideoUrlTimestampButton;",
    )

    execute {
        buttonsDescriptors.forEach { descriptor ->
            PlayerControlsBytecodePatch.initializeControl("$descriptor->initializeButton(Landroid/view/View;)V")
            PlayerControlsBytecodePatch.injectVisibilityCheckCall("$descriptor->changeVisibility(Z)V")
        }
    }
}
