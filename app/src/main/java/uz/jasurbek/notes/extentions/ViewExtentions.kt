package uz.jasurbek.notes.extentions

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import java.util.*

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


fun Fragment.showOptionsAlertDialog(
    array: Array<String>,
    title: String,
    callBack: (post: String) -> Unit
): Unit = with(AlertDialog.Builder(context)) {
    setTitle(title)
    setItems(array) { _, item ->
        callBack(array[item])
    }
    show()
}

fun Fragment.showSingleChoiceDialog(
    array: Array<String>,
    title: String,
    callBack: (hourOffset: Int) -> Unit
): Unit = with(AlertDialog.Builder(context)) {
    setTitle(title)
    setSingleChoiceItems(array, -1) { dialog, position ->
        callBack(position + 1)
        dialog.dismiss()
    }
    show()
}


fun Fragment.showDatePickerDialog(callBack: (calendar: Calendar) -> Unit) {
    val calendar = Calendar.getInstance()
    val dateChooserListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        callBack(calendar)
    }

    DatePickerDialog(
        requireContext(),
        dateChooserListener,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = calendar.timeInMillis
        show()
    }

}

fun Fragment.showTimePickerDialog(chosenDate: Calendar?, callBack: (calendar: Calendar?) -> Unit) {
    val calendar = Calendar.getInstance()

    val timeChooserListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        chosenDate?.set(Calendar.HOUR_OF_DAY, hourOfDay)
        chosenDate?.set(Calendar.MINUTE, minute)
        callBack(chosenDate)
    }

    TimePickerDialog(
        requireContext(),
        timeChooserListener,
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}


fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .apply {
            setAction("Ok") { this.dismiss() }.show()
        }
}