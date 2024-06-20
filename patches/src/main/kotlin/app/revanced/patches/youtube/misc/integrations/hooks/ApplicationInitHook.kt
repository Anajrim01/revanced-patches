package app.revanced.patches.youtube.misc.integrations.hooks

import app.revanced.patches.shared.misc.integrations.integrationsHook

/**
 * Hooks the context when the app is launched as a regular application (and is not an embedded video playback).
 */
// Integrations context is the Activity itself.
internal val applicationInitHook = integrationsHook {
    strings("Application creation", "Application.onCreate")
}
