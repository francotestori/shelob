package generators

import java.io.{ BufferedInputStream, FileInputStream, FileOutputStream }
import java.util.zip.{ ZipEntry, ZipOutputStream }

/**
 * Created by franco on 3/9/2015.
 */
object ZipGenerator {

  def zip(out: String, files: Iterable[String]) = {

    val zip = new ZipOutputStream(new FileOutputStream(out))

    files.foreach { name =>
      zip.putNextEntry(new ZipEntry(name))
      val in = new BufferedInputStream(new FileInputStream(name))
      var b = in.read()
      while (b > -1) {
        zip.write(b)
        b = in.read()
      }
      in.close()
      zip.closeEntry()
    }
    zip.close()
  }

}
