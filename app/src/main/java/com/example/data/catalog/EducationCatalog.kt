package com.example.data.catalog

import com.example.domain.model.CatalogSubject
import com.example.domain.model.EducationCountry
import com.example.domain.model.EducationGrade
import com.example.domain.model.EducationSystem

object EducationCatalog {

    val countries: List<EducationCountry> by lazy {
        listOf(
            // 1. Egypt
            EducationCountry(
                id = "eg",
                nameEn = "Egypt",
                nameAr = "مصر",
                icon = "🇪🇬",
                systems = listOf(
                    EducationSystem(
                        id = "eg_general",
                        countryId = "eg",
                        nameEn = "General Secondary (Thanawiya)",
                        nameAr = "الثانوية العامة المصرية",
                        descriptionEn = "Egyptian General Secondary Education",
                        descriptionAr = "مرحلة الثانوية العامة المصرية بصفوفها الثلاثة",
                        grades = listOf(
                            EducationGrade(
                                id = "eg_sec_1",
                                systemId = "eg_general",
                                nameEn = "1st Secondary (Grade 10)",
                                nameAr = "الصف الأول الثانوي",
                                subjects = listOf(
                                    CatalogSubject("eg_s1_ar", "Arabic", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("eg_s1_en", "English", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("eg_s1_fr", "2nd Foreign Language", "اللغة الأجنبية الثانية", 0xFF8B5CF6),
                                    CatalogSubject("eg_s1_math", "Mathematics", "الرياضيات", 0xFFF59E0B),
                                    CatalogSubject("eg_s1_physics", "Physics", "الفيزياء", 0xFFEC4899),
                                    CatalogSubject("eg_s1_chem", "Chemistry", "الكيمياء", 0xFF06B6D4),
                                    CatalogSubject("eg_s1_bio", "Biology", "الأحياء", 0xFF14B8A6),
                                    CatalogSubject("eg_s1_history", "History", "التاريخ", 0xFF84CC16),
                                    CatalogSubject("eg_s1_geog", "Geography", "الجغرافيا", 0xFFF97316),
                                    CatalogSubject("eg_s1_phil", "Philosophy", "الفلسفة والمنطق", 0xFF6366F1)
                                )
                            ),
                            EducationGrade(
                                id = "eg_sec_2_sci",
                                systemId = "eg_general",
                                nameEn = "2nd Secondary - Science",
                                nameAr = "الصف الثاني الثانوي - علمي",
                                subjects = listOf(
                                    CatalogSubject("eg_s2_ar", "Arabic", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("eg_s2_en", "English", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("eg_s2_fr", "2nd Foreign Language", "اللغة الأجنبية الثانية", 0xFF8B5CF6),
                                    CatalogSubject("eg_s2_pure_math", "Pure Mathematics", "الرياضيات البحتة", 0xFFF59E0B),
                                    CatalogSubject("eg_s2_app_math", "Applied Mathematics", "تطبيقات الرياضيات", 0xFFEAB308),
                                    CatalogSubject("eg_s2_physics", "Physics", "الفيزياء", 0xFFEC4899),
                                    CatalogSubject("eg_s2_chem", "Chemistry", "الكيمياء", 0xFF06B6D4),
                                    CatalogSubject("eg_s2_bio", "Biology", "الأحياء", 0xFF14B8A6)
                                )
                            ),
                            EducationGrade(
                                id = "eg_sec_2_art",
                                systemId = "eg_general",
                                nameEn = "2nd Secondary - Arts/Lit",
                                nameAr = "الصف الثاني الثانوي - أدبي",
                                subjects = listOf(
                                    CatalogSubject("eg_s2_ar", "Arabic", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("eg_s2_en", "English", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("eg_s2_fr", "2nd Foreign Language", "اللغة الأجنبية الثانية", 0xFF8B5CF6),
                                    CatalogSubject("eg_s2_gen_math", "General Math", "الرياضيات العامة", 0xFFF59E0B),
                                    CatalogSubject("eg_s2_history", "History", "التاريخ", 0xFF84CC16),
                                    CatalogSubject("eg_s2_geog", "Geography", "الجغرافيا", 0xFFF97316),
                                    CatalogSubject("eg_s2_phil", "Philosophy & Logic", "الفلسفة والمنطق", 0xFF6366F1),
                                    CatalogSubject("eg_s2_psych", "Psychology & Sociology", "علم النفس والاجتماع", 0xFFD946EF)
                                )
                            ),
                            EducationGrade(
                                id = "eg_sec_3_sci_med",
                                systemId = "eg_general",
                                nameEn = "3rd Secondary - Science (Medical)",
                                nameAr = "الصف الثالث الثانوي - علمي علوم",
                                subjects = listOf(
                                    CatalogSubject("eg_s3_ar", "Arabic", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("eg_s3_en", "English", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("eg_s3_fr", "2nd Foreign Language", "اللغة الأجنبية الثانية", 0xFF8B5CF6),
                                    CatalogSubject("eg_s3_chem", "Chemistry", "الكيمياء", 0xFF06B6D4),
                                    CatalogSubject("eg_s3_physics", "Physics", "الفيزياء", 0xFFEC4899),
                                    CatalogSubject("eg_s3_bio", "Biology", "الأحياء", 0xFF14B8A6),
                                    CatalogSubject("eg_s3_geol", "Geology & Env Science", "الجيولوجيا وعلوم البيئة", 0xFF059669)
                                )
                            ),
                            EducationGrade(
                                id = "eg_sec_3_sci_math",
                                systemId = "eg_general",
                                nameEn = "3rd Secondary - Math (Engineering)",
                                nameAr = "الصف الثالث الثانوي - علمي رياضة",
                                subjects = listOf(
                                    CatalogSubject("eg_s3_ar", "Arabic", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("eg_s3_en", "English", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("eg_s3_fr", "2nd Foreign Language", "اللغة الأجنبية الثانية", 0xFF8B5CF6),
                                    CatalogSubject("eg_s3_chem", "Chemistry", "الكيمياء", 0xFF06B6D4),
                                    CatalogSubject("eg_s3_physics", "Physics", "الفيزياء", 0xFFEC4899),
                                    CatalogSubject("eg_s3_pure_math", "Pure Math (Algebra & Calc)", "الرياضيات البحتة", 0xFFF59E0B),
                                    CatalogSubject("eg_s3_app_math", "Applied Math (Statics & Dynamics)", "الرياضيات التطبيقية", 0xFFEAB308)
                                )
                            ),
                            EducationGrade(
                                id = "eg_sec_3_art",
                                systemId = "eg_general",
                                nameEn = "3rd Secondary - Arts/Literature",
                                nameAr = "الصف الثالث الثانوي - أدبي",
                                subjects = listOf(
                                    CatalogSubject("eg_s3_ar", "Arabic", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("eg_s3_en", "English", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("eg_s3_fr", "2nd Foreign Language", "اللغة الأجنبية الثانية", 0xFF8B5CF6),
                                    CatalogSubject("eg_s3_history", "History", "التاريخ", 0xFF84CC16),
                                    CatalogSubject("eg_s3_geog", "Geography", "الجغرافيا", 0xFFF97316),
                                    CatalogSubject("eg_s3_phil", "Philosophy & Logic", "الفلسفة والمنطق", 0xFF6366F1),
                                    CatalogSubject("eg_s3_psych", "Psychology & Sociology", "علم النفس والاجتماع", 0xFFD946EF)
                                )
                            )
                        )
                    ),
                    EducationSystem(
                        id = "eg_prep",
                        countryId = "eg",
                        nameEn = "Preparatory School (Middle)",
                        nameAr = "المرحلة الإعدادية",
                        descriptionEn = "Egyptian Preparatory Education (Grades 7-9)",
                        descriptionAr = "المرحلة الإعدادية بصفوفها الثلاثة",
                        grades = listOf(
                            EducationGrade(
                                id = "eg_prep_1",
                                systemId = "eg_prep",
                                nameEn = "1st Preparatory (Grade 7)",
                                nameAr = "الصف الأول الإعدادي",
                                subjects = listOf(
                                    CatalogSubject("eg_p1_ar", "Arabic", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("eg_p1_en", "English", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("eg_p1_math", "Mathematics", "الرياضيات", 0xFFF59E0B),
                                    CatalogSubject("eg_p1_sci", "Science", "العلوم", 0xFF06B6D4),
                                    CatalogSubject("eg_p1_soc", "Social Studies", "الدراسات الاجتماعية", 0xFFF97316),
                                    CatalogSubject("eg_p1_comp", "Computer & IT", "الحاسب الآلي وتكنولوجيا المعلومات", 0xFF8B5CF6)
                                )
                            ),
                            EducationGrade(
                                id = "eg_prep_2",
                                systemId = "eg_prep",
                                nameEn = "2nd Preparatory (Grade 8)",
                                nameAr = "الصف الثاني الإعدادي",
                                subjects = listOf(
                                    CatalogSubject("eg_p2_ar", "Arabic", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("eg_p2_en", "English", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("eg_p2_math", "Mathematics", "الرياضيات", 0xFFF59E0B),
                                    CatalogSubject("eg_p2_sci", "Science", "العلوم", 0xFF06B6D4),
                                    CatalogSubject("eg_p2_soc", "Social Studies", "الدراسات الاجتماعية", 0xFFF97316),
                                    CatalogSubject("eg_p2_comp", "Computer & IT", "الحاسب الآلي وتكنولوجيا المعلومات", 0xFF8B5CF6)
                                )
                            ),
                            EducationGrade(
                                id = "eg_prep_3",
                                systemId = "eg_prep",
                                nameEn = "3rd Preparatory (Grade 9)",
                                nameAr = "الصف الثالث الإعدادي (الشهادة الإعدادية)",
                                subjects = listOf(
                                    CatalogSubject("eg_p3_ar", "Arabic", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("eg_p3_en", "English", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("eg_p3_math", "Mathematics", "الرياضيات", 0xFFF59E0B),
                                    CatalogSubject("eg_p3_sci", "Science", "العلوم", 0xFF06B6D4),
                                    CatalogSubject("eg_p3_soc", "Social Studies", "الدراسات الاجتماعية", 0xFFF97316),
                                    CatalogSubject("eg_p3_comp", "Computer & IT", "الحاسب الآلي وتكنولوجيا المعلومات", 0xFF8B5CF6)
                                )
                            )
                        )
                    )
                )
            ),

            // 2. Saudi Arabia
            EducationCountry(
                id = "sa",
                nameEn = "Saudi Arabia",
                nameAr = "المملكة العربية السعودية",
                icon = "🇸🇦",
                systems = listOf(
                    EducationSystem(
                        id = "sa_masarat",
                        countryId = "sa",
                        nameEn = "High School (Masarat Tracks)",
                        nameAr = "المرحلة الثانوية - نظام المسارات",
                        descriptionEn = "Saudi High School Masarat System",
                        descriptionAr = "نظام المسارات المعتمد لوزارة التعليم السعودية",
                        grades = listOf(
                            EducationGrade(
                                id = "sa_sec_1",
                                systemId = "sa_masarat",
                                nameEn = "Year 1 - Common Track",
                                nameAr = "السنة الأولى المشتركة",
                                subjects = listOf(
                                    CatalogSubject("sa_s1_quran", "Islamic Studies & Quran", "القرآن والدراسات الإسلامية", 0xFF059669),
                                    CatalogSubject("sa_s1_ar", "Arabic Language (Kifayat)", "اللغة العربية (الكفايات)", 0xFF2563EB),
                                    CatalogSubject("sa_s1_en", "English Language", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("sa_s1_math", "Mathematics 1", "الرياضيات 1", 0xFFF59E0B),
                                    CatalogSubject("sa_s1_physics", "Physics 1", "الفيزياء 1", 0xFFEC4899),
                                    CatalogSubject("sa_s1_chem", "Chemistry 1", "الكيمياء 1", 0xFF06B6D4),
                                    CatalogSubject("sa_s1_bio", "Biology 1", "الأحياء 1", 0xFF14B8A6),
                                    CatalogSubject("sa_s1_cs", "Computer & Digital Tech", "التقنية الرقمية", 0xFF8B5CF6),
                                    CatalogSubject("sa_s1_soc", "Social Studies", "الدراسات الاجتماعية", 0xFFF97316)
                                )
                            ),
                            EducationGrade(
                                id = "sa_sec_2_gen",
                                systemId = "sa_masarat",
                                nameEn = "Year 2 - General Track",
                                nameAr = "السنة الثانية - المسار العام",
                                subjects = listOf(
                                    CatalogSubject("sa_s2_math", "Mathematics 2", "الرياضيات 2", 0xFFF59E0B),
                                    CatalogSubject("sa_s2_physics", "Physics 2", "الفيزياء 2", 0xFFEC4899),
                                    CatalogSubject("sa_s2_chem", "Chemistry 2", "الكيمياء 2", 0xFF06B6D4),
                                    CatalogSubject("sa_s2_bio", "Biology 2", "الأحياء 2", 0xFF14B8A6),
                                    CatalogSubject("sa_s2_en", "English Language 2", "اللغة الإنجليزية 2", 0xFF10B981),
                                    CatalogSubject("sa_s2_dig", "Digital Tech 2", "التقنية الرقمية 2", 0xFF8B5CF6),
                                    CatalogSubject("sa_s2_art", "Arts & Self-development", "الفنون والمهارات الحياتية", 0xFFD946EF)
                                )
                            ),
                            EducationGrade(
                                id = "sa_sec_3_cs",
                                systemId = "sa_masarat",
                                nameEn = "Year 3 - Computer & Engineering",
                                nameAr = "السنة الثالثة - مسار علوم الحاسب والهندسة",
                                subjects = listOf(
                                    CatalogSubject("sa_s3_math", "Mathematics 3", "الرياضيات 3", 0xFFF59E0B),
                                    CatalogSubject("sa_s3_physics", "Physics 3", "الفيزياء 3", 0xFFEC4899),
                                    CatalogSubject("sa_s3_ai", "Artificial Intelligence & Robotics", "الذكاء الاصطناعي وهندسة البرمجيات", 0xFF6366F1),
                                    CatalogSubject("sa_s3_cyber", "Cybersecurity & Data", "الأمن السيبراني وعلم البيانات", 0xFF06B6D4),
                                    CatalogSubject("sa_s3_en", "English Language 3", "اللغة الإنجليزية 3", 0xFF10B981),
                                    CatalogSubject("sa_s3_research", "Scientific Research", "البحث ومصادر المعلومات", 0xFF84CC16)
                                )
                            )
                        )
                    )
                )
            ),

            // 3. International & General Systems
            EducationCountry(
                id = "intl",
                nameEn = "International & General",
                nameAr = "أنظمة دولية وعامة",
                icon = "🌐",
                systems = listOf(
                    EducationSystem(
                        id = "intl_high",
                        countryId = "intl",
                        nameEn = "General High School / Secondary",
                        nameAr = "المرحلة الثانوية العامة (دولي / عام)",
                        descriptionEn = "Standard International Secondary Curriculum",
                        descriptionAr = "المناهج العامة القياسية للمرحلة الثانوية",
                        grades = listOf(
                            EducationGrade(
                                id = "intl_g10",
                                systemId = "intl_high",
                                nameEn = "Grade 10 / Year 11",
                                nameAr = "الصف العاشر",
                                subjects = listOf(
                                    CatalogSubject("in_g10_math", "Mathematics", "الرياضيات", 0xFFF59E0B),
                                    CatalogSubject("in_g10_sci", "Science (Physics / Chem / Bio)", "العلوم الطبيعية", 0xFF06B6D4),
                                    CatalogSubject("in_g10_eng", "English Language & Lit", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("in_g10_ar", "Arabic Language", "اللغة العربية", 0xFF2563EB),
                                    CatalogSubject("in_g10_soc", "Social Studies / History", "التاريخ والدراسات الاجتماعية", 0xFFF97316),
                                    CatalogSubject("in_g10_cs", "Computer Science", "علوم الحاسب", 0xFF8B5CF6)
                                )
                            ),
                            EducationGrade(
                                id = "intl_g11",
                                systemId = "intl_high",
                                nameEn = "Grade 11 / Year 12",
                                nameAr = "الصف الحادي عشر",
                                subjects = listOf(
                                    CatalogSubject("in_g11_math", "Advanced Math / Calculus", "الرياضيات المتقدمة", 0xFFF59E0B),
                                    CatalogSubject("in_g11_phy", "Physics", "الفيزياء", 0xFFEC4899),
                                    CatalogSubject("in_g11_chem", "Chemistry", "الكيمياء", 0xFF06B6D4),
                                    CatalogSubject("in_g11_bio", "Biology", "الأحياء", 0xFF14B8A6),
                                    CatalogSubject("in_g11_eng", "English Academic", "اللغة الإنجليزية الأكاديمية", 0xFF10B981),
                                    CatalogSubject("in_g11_cs", "Computer Science / IT", "تكنولوجيا المعلومات", 0xFF8B5CF6)
                                )
                            ),
                            EducationGrade(
                                id = "intl_g12",
                                systemId = "intl_high",
                                nameEn = "Grade 12 / Senior Year",
                                nameAr = "الصف الثاني عشر (التخرج)",
                                subjects = listOf(
                                    CatalogSubject("in_g12_math", "Senior Mathematics", "الرياضيات", 0xFFF59E0B),
                                    CatalogSubject("in_g12_phy", "Physics", "الفيزياء", 0xFFEC4899),
                                    CatalogSubject("in_g12_chem", "Chemistry", "الكيمياء", 0xFF06B6D4),
                                    CatalogSubject("in_g12_bio", "Biology", "الأحياء", 0xFF14B8A6),
                                    CatalogSubject("in_g12_eng", "English Composition", "اللغة الإنجليزية", 0xFF10B981),
                                    CatalogSubject("in_g12_econ", "Economics / Business", "الاقتصاد وإدارة الأعمال", 0xFF84CC16)
                                )
                            )
                        )
                    ),
                    EducationSystem(
                        id = "intl_uni",
                        countryId = "intl",
                        nameEn = "University / College Student",
                        nameAr = "المرحلة الجامعية والدراسات العليا",
                        descriptionEn = "University Academic Programs",
                        descriptionAr = "الدراسة الجامعية والأكاديمية بمختلف التخصصات",
                        grades = listOf(
                            EducationGrade(
                                id = "uni_custom",
                                systemId = "intl_uni",
                                nameEn = "University Program (Custom Subjects)",
                                nameAr = "المرحلة الجامعية (إضافة المواد يدويًا)",
                                subjects = emptyList() // Intentionally empty to trigger the custom subjects fallback
                            )
                        )
                    )
                )
            ),

            // 4. Custom / Other
            EducationCountry(
                id = "custom",
                nameEn = "Other System / Custom",
                nameAr = "نظام دراسي آخر / يدوي",
                icon = "✏️",
                systems = listOf(
                    EducationSystem(
                        id = "custom_sys",
                        countryId = "custom",
                        nameEn = "Custom / Unlisted System",
                        nameAr = "نظام دراسي غير مدرج",
                        descriptionEn = "Add your own personalized subjects",
                        descriptionAr = "إدخال وتخصيص المواد الدراسية الخاصة بك يدويًا",
                        grades = listOf(
                            EducationGrade(
                                id = "custom_grade",
                                systemId = "custom_sys",
                                nameEn = "General Custom Grade",
                                nameAr = "مرحلة دراسية مخصصة",
                                subjects = emptyList() // Intentionally empty to trigger custom subjects creation
                            )
                        )
                    )
                )
            )
        )
    }

    fun findCountry(countryId: String?): EducationCountry? {
        return countries.firstOrNull { it.id == countryId }
    }

    fun findSystem(countryId: String?, systemId: String?): EducationSystem? {
        val country = findCountry(countryId) ?: return null
        return country.systems.firstOrNull { it.id == systemId }
    }

    fun findGrade(countryId: String?, systemId: String?, gradeId: String?): EducationGrade? {
        val system = findSystem(countryId, systemId) ?: return null
        return system.grades.firstOrNull { it.id == gradeId }
    }
}
