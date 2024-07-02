package org.membraneframework.rtc.models

import org.membraneframework.rtc.SimulcastConfig
import org.membraneframework.rtc.utils.Metadata

/**
 * Class containing information about track
 *
 * @property metadata metadata of given track
 * @property simulcastConfig contains information regarding simulcast, including whether it is enabled and data on its supported encodings.
 *
 */

data class TrackData(val metadata: Metadata?, val simulcastConfig: SimulcastConfig?)
