import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.ui.MTR.MtrDetailViewModel

class MtrDetailViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MtrDetailViewModel::class.java)) {
            val db = AppDatabase.getDatabase(context)
            val mtrDao = db.mtrDao()
            @Suppress("UNCHECKED_CAST")
            return MtrDetailViewModel(mtrDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
