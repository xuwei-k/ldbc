/** This file is part of the ldbc. For the full copyright and license information, please view the LICENSE file that was
  * distributed with this source code.
  */

package ldbc

import javax.sql.DataSource

import scala.deriving.Mirror

import cats.data.Kleisli
import cats.implicits.*

import cats.effect.{ IO, Resource, Sync }
import cats.effect.kernel.Resource.ExitCase

import ldbc.sql.*
import ldbc.dsl.syntax.*
import ldbc.dsl.logging.{ LogEvent, LogHandler }
import ldbc.query.builder.syntax.ColumnSyntax

package object dsl:

  private trait SyncSyntax[F[_]: Sync]
    extends SQLSyntax[F],
            ConnectionSyntax[F],
            ColumnSyntax[F],
            QuerySyntax[F],
            CommandSyntax[F]:

    implicit class SqlOps(sql: SQL[F]):
      inline def query[T <: Tuple]: Command[F, T] =
        Command(sql.statement, sql.params, Kleisli { resultSet =>
          ResultSetReader.fold[F, T].toList.zipWithIndex.traverse {
            case (reader, index) => reader.asInstanceOf[ResultSetReader[F, Any]].read(resultSet, index + 1)
          }.map(list => Tuple.fromArray(list.toArray).asInstanceOf[T])
        })

      inline def query[P <: Product](using mirror: Mirror.ProductOf[P]): Command[F, P] =
        Command(sql.statement, sql.params, Kleisli { resultSet =>
          ResultSetReader.fold[F, mirror.MirroredElemTypes].toList.zipWithIndex.traverse {
            case (reader, index) => reader.asInstanceOf[ResultSetReader[F, Any]].read(resultSet, index + 1)
          }.map(list => mirror.fromProduct(Tuple.fromArray(list.toArray)))
        })

      def update(using logHandler: LogHandler[F]): Kleisli[F, Connection[F], Int] = Kleisli { connection =>
        (for
          statement <- connection.prepareStatement(sql.statement)
          result <- sql.params.zipWithIndex.traverse {
            case (param, index) => param.bind(statement, index + 1)
          } >> statement.executeUpdate()
        yield result)
          .onError(ex => logHandler.run(LogEvent.ExecFailure(sql.statement, sql.params.map(_.parameter).toList, ex)))
          <* logHandler.run(LogEvent.Success(sql.statement, sql.params.map(_.parameter).toList))
      }

    private def buildConnectionResource(acquire: F[Connection[F]]): Resource[F, Connection[F]] =
      val release: Connection[F] => F[Unit] = connection => connection.close()
      Resource.make(acquire)(release)

    extension [T](connectionKleisli: Kleisli[F, Connection[F], T])

      def readOnly: Kleisli[F, DataSource, T] = Kleisli { dataSource =>
        buildConnectionResource {
          for
            connection <- Sync[F].blocking(dataSource.getConnection).map(ConnectionIO[F])
            _          <- connection.setReadOnly(true)
          yield connection
        }
          .use(connectionKleisli.run)
      }

      def autoCommit: Kleisli[F, DataSource, T] = Kleisli { dataSource =>
        buildConnectionResource {
          for
            connection <- Sync[F].blocking(dataSource.getConnection).map(ConnectionIO[F])
            _          <- connection.setReadOnly(false) >> connection.setAutoCommit(true)
          yield connection
        }
          .use(connectionKleisli.run)
      }

      def transaction: Kleisli[F, DataSource, T] = Kleisli { dataSource =>
        (for
          connection <- buildConnectionResource {
                          for
                            connection <- Sync[F].blocking(dataSource.getConnection).map(ConnectionIO[F])
                            _          <- connection.setReadOnly(false) >> connection.setAutoCommit(false)
                          yield connection
                        }
          transact <- Resource.makeCase(Sync[F].pure(connection)) {
                        case (conn, ExitCase.Errored(e)) => conn.rollback() >> Sync[F].raiseError(e)
                        case (conn, _)                   => conn.commit()
                      }
        yield transact).use(connectionKleisli.run)
      }

  val io: SyncSyntax[IO] = new SyncSyntax[IO] {}
