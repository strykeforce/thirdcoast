package org.strykeforce.json

import com.ctre.phoenix6.configs.AudioConfigs
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Audio(
  @Json(name = "allowMusicDuringDisable") val allowMusicDuringDisable: Int = 0,
  @Json(name = "beepOnBoot") val beepOnBoot: Int = 1,
  @Json(name = "beepOnConfig") val beepOnConfig: Int = 1,
) {

  fun getAudioCOnfig(): AudioConfigs {
    var disableMusic: Boolean = false
    if (allowMusicDuringDisable == 1) disableMusic = true

    var beepBoot: Boolean = true
    if (beepOnBoot == 0) beepBoot = false

    var beepConfig: Boolean = true
    if (beepOnConfig == 0) beepConfig = false

    return AudioConfigs()
      .withAllowMusicDurDisable(disableMusic)
      .withBeepOnBoot(beepBoot)
      .withBeepOnConfig(beepConfig)
  }
}
