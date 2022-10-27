package Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soomgodev.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMember> myDataList = null;
    Context context;


    ChatAdapter(ArrayList<ChatMember> dataList)
    {
        myDataList = dataList;
    }

    public ChatAdapter(Context context, List<ChatMember> myDataList) {
        this.context = context;
        this.myDataList = myDataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == ViewType.CENTER_JOIN)
        {
            view = inflater.inflate(R.layout.item_continer_center_join, parent, false);
            return new CenterViewHolder(view);
        }
        else if(viewType == ViewType.LEFT_CHAT)
        {
            view = inflater.inflate(R.layout.item_continer_received_message, parent, false);
            return new LeftViewHolder(view);
        }
        else if(viewType == ViewType.RIGHT_CHAT_UNREAD)
        {
            view = inflater.inflate(R.layout.item_continer_sent_message_unread, parent, false);
            return new RightUnreadViewHolder(view);
        }
        else
        {
            view = inflater.inflate(R.layout.item_continer_sent_message, parent, false);
            return new RightViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        if(viewHolder instanceof CenterViewHolder)
        {
            ((CenterViewHolder) viewHolder).message.setText(myDataList.get(position).getTime());
        }
        else if(viewHolder instanceof LeftViewHolder)
        {
            ((LeftViewHolder) viewHolder).nickname.setText(myDataList.get(position).getNickname());
            ((LeftViewHolder) viewHolder).message.setText(myDataList.get(position).getMessage());
            ((LeftViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
            ((LeftViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());


            Glide.with(((LeftViewHolder) viewHolder).itemView.getContext())
                    .load(myDataList.get(position).getUserProfileImage())
                    .centerCrop()
                    .into(((LeftViewHolder) viewHolder).imageProfile);

        }
        else if(viewHolder instanceof RightUnreadViewHolder)
        {
            ((RightUnreadViewHolder) viewHolder).message.setText(myDataList.get(position).getMessage());
            ((RightUnreadViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
//            ((RightUnreadViewHolder) viewHolder).readOrNot.setText(myDataList.get(position).getReadOrNot());
            ((RightUnreadViewHolder) viewHolder).readOrNot.setText("안읽음");
        }
        else
        {
            ((RightViewHolder) viewHolder).message.setText(myDataList.get(position).getMessage());
            ((RightViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
        }
    }

    @Override
    public int getItemCount()
    {
        return myDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return myDataList.get(position).getViewType();
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder{
        TextView message;

        CenterViewHolder(View itemView)
        {
            super(itemView);

            message = itemView.findViewById(R.id.message);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView nickname;
        TextView time;
        ImageView imageProfile;

        LeftViewHolder(View itemView)
        {
            super(itemView);

            message = itemView.findViewById(R.id.message);
            nickname = itemView.findViewById(R.id.nickname);
            time = itemView.findViewById(R.id.time);
            imageProfile = itemView.findViewById(R.id.imageProfile);

        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView time;

        RightViewHolder(View itemView)
        {
            super(itemView);

            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }

    public class RightUnreadViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView time;
        TextView readOrNot;

        RightUnreadViewHolder(View itemView)
        {
            super(itemView);

            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            readOrNot = itemView.findViewById(R.id.readOrNot);
        }
    }
}