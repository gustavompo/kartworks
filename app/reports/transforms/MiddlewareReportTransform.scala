package reports.transforms

trait MiddlewareReportTransform[I, O] {
  def map(items: List[I]): List[O]
}



