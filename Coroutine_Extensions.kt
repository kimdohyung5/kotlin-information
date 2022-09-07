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

// 위로 이용하는 것이 좋을 듯 하다... ( 위랑 동일한 코드이다.. )
viewLifeOwner.lifecycleScope.launchWhenStarted {
	viewModel.breakingNews.collect { articles ->
		adapter.submitList(articles)
	}	
}

val breakingNews = repository.getBreakingNews()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

	
// 또다른 케이스
fun <T> FavoriteFragment.collectLatestStateFlow(flow: Flow<T>, collect: suspend (T) -> Unit ) {
    viewLifecycleOwner.lifecycleScope.launch { 
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}
