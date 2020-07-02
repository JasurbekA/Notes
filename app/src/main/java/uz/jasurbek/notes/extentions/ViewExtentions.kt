package uz.jasurbek.notes.extentions

import android.os.Bundle
import android.widget.Toast
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

fun Fragment.toast(message: String) =
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()


