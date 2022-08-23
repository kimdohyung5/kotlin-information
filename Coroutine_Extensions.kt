// 사용하기
collectLatestStateFlow(bookSearchViewModel.searchPagingResult) {
	bookSearchAdapter.submitData(it)
}
		
// 구현하기.		
fun <T> SearchFragment.collectLatestStateFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}
