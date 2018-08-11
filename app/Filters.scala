import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter
import play.filters.gzip.GzipFilter
import play.filters.headers.SecurityHeadersFilter

import filters.PragmaFilter
import javax.inject.Inject

/**
 * Add the following filters by default to all projects
 * 
 * https://www.playframework.com/documentation/latest/ScalaCsrf 
 * https://www.playframework.com/documentation/latest/AllowedHostsFilter
 * https://www.playframework.com/documentation/latest/SecurityHeaders
 */
class Filters @Inject() (
  securityHeadersFilter: SecurityHeadersFilter,
  corsFilter: CORSFilter,
  gzipFilter: GzipFilter,
  pragmaFilter: PragmaFilter
) extends DefaultHttpFilters(
  securityHeadersFilter,
//  corsFilter,
  gzipFilter,
  pragmaFilter
)
