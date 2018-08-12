package utils

object ImplicitUtils {
  implicit class StringUtils(str: String) {
    def timeStringAsMillis = {
      val spl = str.split(':')
      val sMil = spl(1).split('.')
      spl(0).toLong * 60 * 1000 + sMil(0).toLong * 1000 + sMil(1).toLong
    }
  }
}

