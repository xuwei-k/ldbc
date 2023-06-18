/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.generator.model

import ldbc.core.JdbcType

enum ScalaType:
  case Byte, String, Short, Int, Long, BigDecimal, Float, BigInt

trait DataType:

  val name: String

  val jdbcType: JdbcType

  val scalaType: ScalaType

  def toCode(typeParam: String): String

object DataType:

  def BIT(length: Int): DataType = new DataType:
    override val name:      String    = "BIT"
    override val jdbcType:  JdbcType  = JdbcType.Bit
    override val scalaType: ScalaType = ScalaType.Byte

    override def toCode(typeParam: String): String = s"$name[$typeParam]($length)"

  def TINYINT(length: Int, unsigned: Boolean, zerofill: Boolean): DataType = new DataType:
    override val name:      String    = "TINYINT"
    override val jdbcType:  JdbcType  = JdbcType.TinyInt
    override val scalaType: ScalaType = if unsigned then ScalaType.Short else ScalaType.Byte

    override def toCode(typeParam: String): String =
      (unsigned, zerofill) match
        case (true, true)   => s"$name[$typeParam]($length).UNSIGNED.ZEROFILL"
        case (true, false)  => s"$name[$typeParam]($length).UNSIGNED"
        case (false, true)  => s"$name[$typeParam]($length).ZEROFILL"
        case (false, false) => s"$name[$typeParam]($length)"

  def SMALLINT(length: Int, unsigned: Boolean, zerofill: Boolean): DataType = new DataType:
    override val name:      String    = "SMALLINT"
    override val jdbcType:  JdbcType  = JdbcType.SmallInt
    override val scalaType: ScalaType = if unsigned then ScalaType.Int else ScalaType.Short

    override def toCode(typeParam: String): String =
      (unsigned, zerofill) match
        case (true, true)   => s"$name[$typeParam]($length).UNSIGNED.ZEROFILL"
        case (true, false)  => s"$name[$typeParam]($length).UNSIGNED"
        case (false, true)  => s"$name[$typeParam]($length).ZEROFILL"
        case (false, false) => s"$name[$typeParam]($length)"

  def MEDIUMINT(length: Int, unsigned: Boolean, zerofill: Boolean): DataType = new DataType:
    override val name:      String    = "MEDIUMINT"
    override val jdbcType:  JdbcType  = JdbcType.Integer
    override val scalaType: ScalaType = ScalaType.Int

    override def toCode(typeParam: String): String =
      (unsigned, zerofill) match
        case (true, true)   => s"$name[$typeParam]($length).UNSIGNED.ZEROFILL"
        case (true, false)  => s"$name[$typeParam]($length).UNSIGNED"
        case (false, true)  => s"$name[$typeParam]($length).ZEROFILL"
        case (false, false) => s"$name[$typeParam]($length)"

  def INT(length: Int, unsigned: Boolean, zerofill: Boolean): DataType = new DataType:
    override val name:      String    = "INT"
    override val jdbcType:  JdbcType  = JdbcType.Integer
    override val scalaType: ScalaType = if unsigned then ScalaType.Long else ScalaType.Int

    override def toCode(typeParam: String): String =
      (unsigned, zerofill) match
        case (true, true)   => s"$name[$typeParam]($length).UNSIGNED.ZEROFILL"
        case (true, false)  => s"$name[$typeParam]($length).UNSIGNED"
        case (false, true)  => s"$name[$typeParam]($length).ZEROFILL"
        case (false, false) => s"$name[$typeParam]($length)"

  def BIGINT(length: Int, unsigned: Boolean, zerofill: Boolean): DataType = new DataType:
    override val name:      String    = "BIGINT"
    override val jdbcType:  JdbcType  = JdbcType.BigInt
    override val scalaType: ScalaType = if unsigned then ScalaType.BigInt else ScalaType.Long

    override def toCode(typeParam: String): String =
      (unsigned, zerofill) match
        case (true, true)   => s"$name[$typeParam]($length).UNSIGNED.ZEROFILL"
        case (true, false)  => s"$name[$typeParam]($length).UNSIGNED"
        case (false, true)  => s"$name[$typeParam]($length).ZEROFILL"
        case (false, false) => s"$name[$typeParam]($length)"

  def DECIMAL(accuracy: Int, scale: Int, unsigned: Boolean, zerofill: Boolean): DataType = new DataType:
    override val name:      String    = "DECIMAL"
    override val jdbcType:  JdbcType  = JdbcType.Decimal
    override val scalaType: ScalaType = ScalaType.BigDecimal

    override def toCode(typeParam: String): String =
      (unsigned, zerofill) match
        case (true, true)   => s"$name[$typeParam]($accuracy, $scale).UNSIGNED.ZEROFILL"
        case (true, false)  => s"$name[$typeParam]($accuracy, $scale).UNSIGNED"
        case (false, true)  => s"$name[$typeParam]($accuracy, $scale).ZEROFILL"
        case (false, false) => s"$name[$typeParam]($accuracy, $scale)"

  def FLOAT(accuracy: Int, unsigned: Boolean, zerofill: Boolean): DataType = new DataType:
    override val name:      String    = "FLOAT"
    override val jdbcType:  JdbcType  = JdbcType.Float
    override val scalaType: ScalaType = ScalaType.Float

    override def toCode(typeParam: String): String =
      (unsigned, zerofill) match
        case (true, true)   => s"$name[$typeParam]($accuracy).UNSIGNED.ZEROFILL"
        case (true, false)  => s"$name[$typeParam]($accuracy).UNSIGNED"
        case (false, true)  => s"$name[$typeParam]($accuracy).ZEROFILL"
        case (false, false) => s"$name[$typeParam]($accuracy)"
