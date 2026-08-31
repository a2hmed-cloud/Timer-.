package com.example.domain.model

data class EducationCountry(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val icon: String,
    val systems: List<EducationSystem>
)

data class EducationSystem(
    val id: String,
    val countryId: String,
    val nameEn: String,
    val nameAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val grades: List<EducationGrade>
)

data class EducationGrade(
    val id: String,
    val systemId: String,
    val nameEn: String,
    val nameAr: String,
    val subjects: List<CatalogSubject>
)

data class CatalogSubject(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val colorHex: Long,
    val icon: String = "book",
    val isDefaultSelected: Boolean = true
)
