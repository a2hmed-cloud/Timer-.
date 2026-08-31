package com.example.data.repository

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val sourceUrl: String?,
    val publishedAt: Long,
    val author: String?
)

interface NewsDataSource {
    suspend fun fetchLatestNews(): List<NewsArticle>
}

class RemoteNewsDataSource : NewsDataSource {
    override suspend fun fetchLatestNews(): List<NewsArticle> {
        // Ready for real HTTP/RSS educational API integration.
        // No fake data or mock news generated.
        return emptyList()
    }
}

class NewsRepository(
    private val remoteDataSource: NewsDataSource = RemoteNewsDataSource()
) {
    suspend fun getLatestNews(): Result<List<NewsArticle>> {
        return try {
            val articles = remoteDataSource.fetchLatestNews()
            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
