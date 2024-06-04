package app.revanced.patches.youtube.misc.fix.backtoexitgesture

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.fingerprint.MethodFingerprintResult
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.onBackPressedFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.recyclerViewScrollingFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.recyclerViewTopScrollingFingerprint
import app.revanced.patches.youtube.misc.fix.backtoexitgesture.fingerprints.recyclerViewTopScrollingParentFingerprint
import app.revanced.util.resultOrThrow

@Suppress("unused")
internal val fixBackToExitGesturePatch = bytecodePatch(
    description = "Fixes the swipe back to exit gesture.",
) {
    val recyclerViewTopScrollingParentResult by recyclerViewTopScrollingParentFingerprint
    val recyclerViewScrollingResult by recyclerViewScrollingFingerprint
    val onBackPressedResult by onBackPressedFingerprint

    execute { context ->
        recyclerViewTopScrollingFingerprint.apply {
            resolve(context, recyclerViewTopScrollingParentResult.classDef)
        }

        /**
         * Inject a call to a method from the integrations.
         *
         * @param targetMethod The target method to call.
         */
        fun MethodFingerprintResult.injectCall(targetMethod: IntegrationsMethod) = mutableMethod.addInstruction(
            scanResult.patternScanResult!!.endIndex,
            targetMethod.toString(),
        )

        mapOf(
            recyclerViewTopScrollingFingerprint.resultOrThrow() to IntegrationsMethod(
                methodName = "onTopView",
            ),
            recyclerViewScrollingResult to IntegrationsMethod(
                methodName = "onScrollingViews",
            ),
            onBackPressedResult to IntegrationsMethod(
                "p0",
                "onBackPressed",
                "Landroid/app/Activity;",
            ),
        ).forEach { (result, target) -> result.injectCall(target) }
    }
}

/**
 * A reference to a method from the integrations for [fixBackToExitGesturePatch].
 *
 * @param register The method registers.
 * @param methodName The method name.
 * @param parameterTypes The parameters of the method.
 */
private class IntegrationsMethod(
    val register: String = "",
    val methodName: String,
    val parameterTypes: String = "",
) {
    override fun toString() =
        "invoke-static {$register}, Lapp/revanced/integrations/youtube/patches/FixBackToExitGesturePatch;->$methodName($parameterTypes)V"
}
