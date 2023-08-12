/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc.query.builder.interpreter

import ldbc.query.builder.ColumnReader

object Tuples:

  type MapToColumn[T <: Tuple, F[_]] <: Tuple = T match
    case EmptyTuple      => EmptyTuple
    case h *: EmptyTuple => ColumnReader[F, h] *: EmptyTuple
    case h *: t          => ColumnReader[F, h] *: MapToColumn[t, F]

  type InverseColumnMap[F[_], T <: Tuple] <: Tuple = T match
    case EmptyTuple                       => EmptyTuple
    case ColumnReader[F, h] *: EmptyTuple => h *: EmptyTuple
    case ColumnReader[F, h] *: t          => h *: InverseColumnMap[F, t]

  type ToColumn[F[_], T] = T match
    case Tuple => MapToColumn[T, F]
    case _ => ColumnReader[F, T]
