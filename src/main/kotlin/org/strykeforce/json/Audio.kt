package org.strykeforce.json

import com.ctre.phoenix6.configs.AudioConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Audio(
  @Json(name = "allowMusicDuringDisable") val allowMusicDuringDisable: Boolean = false,
  @Json(name = "beepOnBoot") val beepOnBoot: Boolean = true,
  @Json(name = "beepOnConfig") val beepOnConfig: Boolean = true,
) {

  fun getAudioCOnfig(): AudioConfigs {
    return AudioConfigs()
      .withAllowMusicDurDisable(allowMusicDuringDisable)
      .withBeepOnBoot(beepOnBoot)
      .withBeepOnConfig(beepOnConfig)
  }
}
