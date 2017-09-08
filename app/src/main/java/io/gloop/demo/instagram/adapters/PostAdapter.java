package io.gloop.demo.instagram.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.gloop.GloopList;
import io.gloop.GloopOnChangeListener;
import io.gloop.demo.instagram.R;
import io.gloop.demo.instagram.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ItemViewHolder> {

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        ImageView imageView;

        Post item;

        ItemViewHolder(View view) {
            super(view);
            this.itemNameTextView = (TextView) view.findViewById(R.id.post_item_message);
            this.imageView = (ImageView) view.findViewById(R.id.item_picture);
            view.setClickable(true);
        }
    }

    private GloopList<Post> postList;

    public PostAdapter(GloopList<Post> postList) {
        this.postList = postList;
        this.postList.addOnChangeListener(new GloopOnChangeListener() {

            @Override
            public void onChange() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public PostAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final Post item = this.postList.get(position);

        holder.itemNameTextView.setText(item.getMessage());
        holder.imageView.setImageBitmap(item.getPicture());

        holder.item = item;
    }

    @Override
    public void onViewRecycled (ItemViewHolder holder) {
        holder.item.getPicture().recycle(); // TODO test if helps reducing memory size.
    }

    @Override
    public int getItemCount() {
        return this.postList.size();
    }

}