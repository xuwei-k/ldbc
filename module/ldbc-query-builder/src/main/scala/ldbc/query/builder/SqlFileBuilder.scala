/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.query.builder

import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime

import scala.io.Codec

import ldbc.core.Table

/** Object to generate sql files that write out Create statements for databases and tables.
  */
object SqlFileBuilder:

  private def build(
    output:   String,
    fileName: Option[String],
    database: Database,
    tables:   Table[?]*
  ): Unit =

    val queryString: String =
      s"""/** This file is automatically generated by ldbc based on the source code.
         |  * This file cannot be modified manually. Instead, modify the database and table settings in the source code.
         |  *
         |  * Generated at ${ LocalDateTime.now().toString }
         |  */
         |
         |${ database.queryString };
         |
         |
         |USE `${ database.label }`;
         |
         |${ tables.map(TableQueryBuilder(_).querySting).mkString("\n") }
         |""".stripMargin

    val outputFile = new File(output, s"${ fileName.getOrElse(database.label) }.sql")

    if !outputFile.exists() then
      outputFile.getParentFile.mkdirs()
      outputFile.createNewFile()

    Files.write(outputFile.toPath, queryString.getBytes(summon[Codec].name))

  def build(output: String, database: Database, tables: Table[?]*): Unit = build(output, None, database, tables: _*)
  def build(output: String, fileName: String, database: Database, tables: Table[?]*): Unit =
    build(output, Some(fileName), database, tables: _*)
