import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecobank2.TransactionData
import com.example.ecobank2.R

class TransactionAdapter(private val transactions: List<TransactionData>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.amountTextView.text = "Amount: ${transaction.amount}"
        holder.pointsTextView.text = "Points: ${transaction.pointsEarned}"
        holder.timestampTextView.text = "Date: ${transaction.timestamp.toDate()}"

        if (transaction.amount > 0) {
            holder.iconImageView.setImageResource(R.drawable.green)
            holder.amountTextView.setTextColor(holder.itemView.context.getColor(R.color.green))
        } else {
            holder.iconImageView.setImageResource(R.drawable.red)
            holder.amountTextView.setTextColor(holder.itemView.context.getColor(R.color.red))
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
