package listeners

import android.view.View

abstract class ClickListener {
    open fun onClick(var1: View, var2: Int) {}
    open fun onLongClick(var1: View, var2: Int) {}
}