package io.gloop.demo.instagram.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import io.gloop.Gloop;
import io.gloop.GloopList;
import io.gloop.demo.instagram.R;
import io.gloop.demo.instagram.adapters.PostAdapter;
import io.gloop.demo.instagram.constants.Constants;
import io.gloop.demo.instagram.model.Post;

public class PostsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GloopList<Post> posts;
    private PostAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = view.findViewById(R.id.post_list);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateNewPostFragment();
            }
        });

        mRecyclerView.addOnItemTouchListener(
                new SwipeableRecyclerViewTouchListener(mRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions)
                                    posts.remove(position);
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions)
                                    posts.remove(position);
                            }
                        }));
        return view;
    }


    public void showCreateNewPostFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new NewPostFragment()).commit();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.post_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        posts = Gloop.all(Post.class);
        mAdapter = new PostAdapter(posts);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = mRecyclerView.indexOfChild(view);
                Post selectedPost = posts.get(itemPosition);

                Bundle arguments = new Bundle();
                arguments.putSerializable(Constants.BUNDLE_DETAIL_GROUP, selectedPost);
                PostDetailFragment newFragment = new PostDetailFragment();
                newFragment.setArguments(arguments);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flContent, newFragment);
                transaction.commit();

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Gloop.sync();

//                posts.reload();
// TODO test if reload is enough.
//                posts = Gloop.all(Post.class);
                mAdapter = new PostAdapter(posts);
                mRecyclerView.setAdapter(mAdapter);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem mSearchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return search(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return search(newText);
            }
        });
    }

    public boolean search(String text) {
        if (!text.isEmpty()) {
            posts = Gloop.all(Post.class)
                    .where()
                    .startsWith("message", text)
                    .all();

            mAdapter = new PostAdapter(posts);
            mRecyclerView.setAdapter(mAdapter);
            return true;
        } else {
            posts = Gloop.all(Post.class);
            mAdapter = new PostAdapter(posts);
            mRecyclerView.setAdapter(mAdapter);
        }
        return false;
    }
}