object Deadeye {

  private val cameras = mutableMapOf<String, Camera>()

  fun getCamera(id: String) = cameras.getOrPut(id) { Camera(id) }

}
