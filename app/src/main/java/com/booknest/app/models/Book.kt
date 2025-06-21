package com.booknest.app.models

data class Book(
    val page : Int,
    val numFound : Int,
    val reading_log_entries : List<Logs>
)

data class Logs(
    val work : Work,
    val logged_edition : String?,
    val logged_date : String
)

data class Work (
    val title : String,
    val key : String,
    val author_keys : List<String>,
    val author_names : List<String>,
    val first_publish_year : Int,
    val lending_edition_s : String?,
    val edition_key : List<String>,
    val cover_id : Int,
    val cover_edition_key : String,
)
