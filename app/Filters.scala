import play.api.http.DefaultHttpFilters
import play.filters.gzip.GzipFilter
import play.filters.headers.SecurityHeadersFilter

import javax.inject.Inject

/**
  * Add the following filters by default to all projects
  *
  * https://www.playframework.com/documentation/latest/ScalaCsrf
  * https://www.playframework.com/documentation/latest/AllowedHostsFilter
  * https://www.playframework.com/documentation/latest/SecurityHeaders
  */
class Filters @Inject()(
    securityHeadersFilter: SecurityHeadersFilter,
    gzipFilter: GzipFilter
) extends DefaultHttpFilters(
  securityHeadersFilter,
  gzipFilter
)
