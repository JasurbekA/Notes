package uz.jasurbek.notes.extentions

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

fun Fragment.navigate(
    destinationID: Int,
    bundle: Bundle? = null,
    clearCurrentFragmentFromBackStack: Boolean? = null
) = with(NavHostFragment.findNavController(this)) {
    clearCurrentFragmentFromBackStack?.let { needToClear ->
        if (needToClear) popBackStack()
    }
    navigate(destinationID, bundle)
}

