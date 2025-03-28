import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecobank2.TransactionData
import com.example.ecobank2.R
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val transactions: List<TransactionData>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Format timestamp
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val formattedDate = transaction.timestamp.toDate()?.let { dateFormat.format(it) } ?: "Unknown"

        holder.timestampTextView.text = "Date: $formattedDate"
        holder.amountTextView.text = "Amount: ${transaction.amount}"

        // Use transaction type to determine styling
        if (transaction.type.lowercase() == "earned") {
            holder.pointsTextView.text = "Earned ${transaction.pointsEarned} points"
            holder.iconImageView.setImageResource(R.drawable.green)
            holder.pointsTextView.setTextColor(holder.itemView.context.getColor(R.color.green))
        } else { // "used"
            holder.pointsTextView.text = "Used ${transaction.pointsEarned} points"
            holder.iconImageView.setImageResource(R.drawable.red)
            holder.pointsTextView.setTextColor(holder.itemView.context.getColor(R.color.red))
            holder.amountTextView.text = "${transaction.rewardTitle}"
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountTextView: TextView = view.findViewById(R.id.transaction_amount)
        val pointsTextView: TextView = view.findViewById(R.id.transaction_points)
        val timestampTextView: TextView = view.findViewById(R.id.transaction_date)
        val iconImageView: ImageView = view.findViewById(R.id.transaction_icon)
    }
}
