import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecobank2.VoucherData
import com.example.ecobank2.R
import java.text.SimpleDateFormat
import java.util.*

class VoucherAdapter(private val vouchers: List<VoucherData>) :
    RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.voucher_list_item, parent, false)
        return VoucherViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val voucher = vouchers[position]

        // Set data
        holder.voucherTitle.text = voucher.rewardTitle
        holder.voucherPoints.text = "Points: ${voucher.rewardPoints}"

        // Format timestamp if present
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = voucher.timestamp.toDate()?.let { dateFormat.format(it) } ?: "Unknown"
        holder.voucherDate.text = "Claimed on: $formattedDate"
    }

    override fun getItemCount(): Int = vouchers.size

    class VoucherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val voucherTitle: TextView = view.findViewById(R.id.voucher_title)
        val voucherPoints: TextView = view.findViewById(R.id.voucher_points)
        val voucherDate: TextView = view.findViewById(R.id.voucher_date) // Add this to your layout
    }
}
