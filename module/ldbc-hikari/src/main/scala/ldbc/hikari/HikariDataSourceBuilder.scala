/**
 *  This file is part of the ldbc.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package ldbc.hikari

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import cats.effect.*
import cats.effect.implicits.*

/** A model for building a database. HikariCP construction, thread pool generation for database connection, test
 * connection, etc. are performed via the method.
 *
 * @tparam F
 *   the effect type.
 */
trait HikariDataSourceBuilder[F[_]: Sync] extends HikariConfigBuilder:

  /** Method for generating HikariDataSource with Resource.
   *
   * @param factory
   * Process to generate HikariDataSource
   */
  private def createDataSourceResource(factory: => HikariDataSource): Resource[F, HikariDataSource] =
    Resource.fromAutoCloseable(Sync[F].delay(factory))

  /** Method to generate Config for HikariCP.
   */
  private def buildConfig(): Resource[F, HikariConfig] =
    Sync[F].delay {
      val hikariConfig = makeFromConfig()
      hikariConfig.validate()
      hikariConfig
    }.toResource

  def buildDataSource(): Resource[F, HikariDataSource] =
    for
      hikariConfig <- buildConfig()
      hikariDataSource <- createDataSourceResource(new HikariDataSource(hikariConfig))
    yield hikariDataSource
