/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.core

import org.specs2.mutable.Specification

import ldbc.core.DataType.*
import ldbc.core.Character.*
import ldbc.core.attribute.AutoInc

object ColumnTest extends Specification:

  "Column Test" should {
    "The query string of the Column model generated with only label and DataType matches the specified string." in {

      val column = Column[Long]("id", Bigint(64))

      column.queryString === "`id` BIGINT(64) NOT NULL"
    }

    "The query string of the Column model generated with only label and DataType and comment matches the specified string." in {

      val column = Column[Long]("id", Bigint(64), "identifier")

      column.queryString === "`id` BIGINT(64) NOT NULL COMMENT 'identifier'"
    }

    "The query string of the Column model generated with only label and DataType and attributes matches the specified string." in {

      val column = Column[Long]("id", Bigint(64), AutoInc[Long]())

      column.queryString === "`id` BIGINT(64) NOT NULL AUTO_INCREMENT"
    }

    "The query string of the Column model generated with only label and DataType and attributes and comment matches the specified string." in {

      val column = Column[Long]("id", Bigint(64), "identifier", AutoInc[Long]())

      column.queryString === "`id` BIGINT(64) NOT NULL AUTO_INCREMENT COMMENT 'identifier'"
    }

    "The query string of the Column model generated with only label and DataType matches the specified string." in {

      val column = Column[String]("name", Varchar(255, None, Some(Big5(None))))

      column.queryString === "`name` VARCHAR(255) CHARACTER SET Big5 NOT NULL"
    }

    "The query string of the Column model generated with only label and DataType and comment matches the specified string." in {

      val column = Column[String]("name", Varchar(255, None, Some(Big5(None))), "name")

      column.queryString === "`name` VARCHAR(255) CHARACTER SET Big5 NOT NULL COMMENT 'name'"
    }

    "The query string of the Column model generated with only label and DataType and comment matches the specified string." in {

      val column =
        Column[String]("name", Varchar(255, None, None).CHARACTER_SET(Big5(Some(Big5.Collate.big5_bin))), "name")

      column.queryString === "`name` VARCHAR(255) CHARACTER SET Big5 COLLATE big5_bin NOT NULL COMMENT 'name'"
    }
  }
