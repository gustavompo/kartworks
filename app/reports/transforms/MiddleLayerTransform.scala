package reports.transforms

trait MiddleLayerTransform[I, O] {
  def map(items: List[I]): List[O]
}



