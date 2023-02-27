/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.dsl

import cats.data.Kleisli

import ldbc.sql.{ Connection, ResultSetConsumer }

/** Trait for generating SQL models from string completion knowledge.
  *
  * @tparam F
  *   The effect type
  */
trait SQLSyntax[F[_]]:

  extension (sc: StringContext)
    inline def sql(inline args: ParameterBinder[F]*): SQL[F] =
      val strings     = sc.parts.iterator
      val expressions = args.iterator
      SQL(strings.mkString("?"), expressions.toSeq)

  extension (sql: SQL[F])
    def query[T](using consumer: ResultSetConsumer[F, T]): Kleisli[F, Connection[F], T]

    def update(): Kleisli[F, Connection[F], Int]
