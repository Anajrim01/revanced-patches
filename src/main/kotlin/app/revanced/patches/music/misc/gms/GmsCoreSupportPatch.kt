package app.revanced.patches.music.misc.gms

import app.revanced.patches.music.misc.gms.Constants.MUSIC_PACKAGE_NAME
import app.revanced.patches.music.misc.gms.Constants.REVANCED_MUSIC_PACKAGE_NAME
import app.revanced.patches.music.misc.gms.fingerprints.*
import app.revanced.patches.music.misc.integrations.integrationsPatch
import app.revanced.patches.shared.fingerprints.castContextFetchFingerprint
import app.revanced.patches.shared.fingerprints.castDynamiteModuleV2Fingerprint
import app.revanced.patches.shared.fingerprints.primeMethodFingerprint
import app.revanced.patches.shared.misc.gms.gmsCoreSupportPatch

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = MUSIC_PACKAGE_NAME,
    toPackageName = REVANCED_MUSIC_PACKAGE_NAME,
    primeMethodFingerprint = primeMethodFingerprint,
    earlyReturnFingerprints = setOf(
        castDynamiteModuleV2Fingerprint,
        castContextFetchFingerprint,
    ),
    mainActivityOnCreateFingerprint = musicActivityOnCreateFingerprint,
    integrationsPatch = integrationsPatch,
    gmsCoreSupportResourcePatchFactory = ::gmsCoreSupportResourcePatch,
) {
    compatibleWith(
        MUSIC_PACKAGE_NAME(
            "6.45.54",
            "6.51.53",
            "7.01.53",
            "7.02.52",
            "7.03.52",
        ),
    )
}
