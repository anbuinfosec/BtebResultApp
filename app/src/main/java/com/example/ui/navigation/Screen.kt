package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object GroupSearch : Screen("group_search")
    data object Booklists : Screen("booklists")
    data object RoutineExplorer : Screen("routine_explorer")
    data object InstituteDirectory : Screen("institute_directory")
    data object CgpaCalculator : Screen("cgpa_calculator")
    data object Statistics : Screen("statistics")
    data object SavedRolls : Screen("saved_rolls")
    data object AboutDeveloper : Screen("about_developer")
    data object ContactSupport : Screen("contact_support")

    data object IndividualResult : Screen("individual_result/{roll}?exam={exam}") {
        fun createRoute(roll: String, exam: String): String {
            return "individual_result/$roll?exam=$exam"
        }
    }

    data object InstituteResults : Screen("institute_results/{code}?name={name}") {
        fun createRoute(code: String, name: String): String {
            return "institute_results/$code?name=${android.net.Uri.encode(name)}"
        }
    }
}
